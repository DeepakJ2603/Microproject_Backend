package com.example.dashboard.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dashboard.dto.GitHubSummaryDTO;
import com.example.dashboard.dto.MetricRequest;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.ProductivityMetric;
import com.example.dashboard.exception.BadRequestException;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.ProductivityMetricRepository;

@Service
public class ProductivityService {

    private final ProductivityMetricRepository metricRepository;

    public ProductivityService(ProductivityMetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    // ── Get all metrics for a developer (newest first) ────────────────────────
    public List<ProductivityMetric> getMetricsForDeveloper(Developer developer) {
        return metricRepository.findByDeveloperOrderByMetricDateDesc(developer);
    }

    // ── Alias used by DeveloperController ────────────────────────────────────
    public List<ProductivityMetric> getMetricsForUser(Developer developer) {
        return getMetricsForDeveloper(developer);
    }

    // ── Create or update metric for a given date ──────────────────────────────
    @Transactional
    public ProductivityMetric upsertMetric(Developer developer,
                                           MetricRequest request) {

        LocalDate date = request.getMetricDate() != null
                ? request.getMetricDate()
                : LocalDate.now();

        ProductivityMetric metric = metricRepository
                .findByDeveloperAndMetricDate(developer, date)
                .orElseGet(() -> {
                    ProductivityMetric m = new ProductivityMetric();
                    m.setDeveloper(developer);
                    m.setMetricDate(date);
                    return m;
                });

        // ✅ NO null checks allowed for primitive int
        metric.setCommitsCount(request.getCommitsCount());
        metric.setPrsCount(request.getPrsCount());
        metric.setIssuesCount(request.getIssuesCount());

        metric.recalculate();

        return metricRepository.saveAndFlush(metric);
    }
    
    public GitHubSummaryDTO getGitHubSummaryForDeveloper(Developer developer) {

        List<ProductivityMetric> metrics =
                metricRepository.findByDeveloperOrderByMetricDateDesc(developer);

        int totalCommits = 0;
        int totalPRs = 0;
        int totalIssues = 0;

        for (ProductivityMetric m : metrics) {
            totalCommits += m.getCommitsCount();
            totalPRs += m.getPrsCount();
            totalIssues += m.getIssuesCount();
        }

        double latestScore = metrics.isEmpty()
                ? 0.0
                : metrics.get(0).getProductivityScore();

        return new GitHubSummaryDTO(
                totalCommits,
                totalPRs,
                totalIssues,
                latestScore
        );
    }

    // ── Get metric by ID ──────────────────────────────────────────────────────
    public ProductivityMetric getMetricById(Long id) {
        return metricRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ProductivityMetric", "id", id
                        ));
    }

    // ── Delete a metric ───────────────────────────────────────────────────────
    @Transactional
    public void deleteMetric(Long id, Developer developer) {
        ProductivityMetric metric = getMetricById(id);

        if (!metric.getDeveloper().getId().equals(developer.getId())) {
            throw new BadRequestException(
                    "Not authorized to delete this metric"
            );
        }

        metricRepository.delete(metric);
    }

    // ── Total score ───────────────────────────────────────────────────────────
    public Double getTotalScore(Developer developer) {
        Double score = metricRepository.sumProductivityByDeveloper(developer);
        return score != null ? score : 0.0;
    }

    // ── Alias used by DeveloperController ────────────────────────────────────
    public Double getTotalScoreForUser(Developer developer) {
        return getTotalScore(developer);
    }

    // ── Average score ─────────────────────────────────────────────────────────
    public Double getAverageScore(Developer developer) {
        List<ProductivityMetric> metrics = getMetricsForDeveloper(developer);
        if (metrics.isEmpty()) {
            return 0.0;
        }
        return metrics.stream()
                .mapToDouble(ProductivityMetric::getProductivityScore)
                .average()
                .orElse(0.0);
    }

    // ── Alias used by DeveloperController ─────────────────────────────────────
    public Double getAverageScoreForUser(Developer developer) {
        return getAverageScore(developer);
    }

    // ── Metrics within date range ─────────────────────────────────────────────
    public List<ProductivityMetric> getMetricsByDateRange(
            Developer developer,
            LocalDate startDate,
            LocalDate endDate) {

        return metricRepository.findByDeveloperAndDateRange(
                developer, startDate, endDate
        );
    }
}
