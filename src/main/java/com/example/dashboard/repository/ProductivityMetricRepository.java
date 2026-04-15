package com.example.dashboard.repository;

import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.ProductivityMetric;
import com.example.dashboard.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductivityMetricRepository
        extends JpaRepository<ProductivityMetric, Long> {

    // ── Find all metrics for a developer sorted newest first ──────────────────
    List<ProductivityMetric> findByDeveloperOrderByMetricDateDesc(
            Developer developer);

    // ── Find metric for a specific developer + date (for upsert) ─────────────
    Optional<ProductivityMetric> findByDeveloperAndMetricDate(
            Developer developer, LocalDate metricDate);

    // ── Sum total productivity score for a developer ──────────────────────────
    @Query("SELECT SUM(p.productivityScore) " +
           "FROM ProductivityMetric p " +
           "WHERE p.developer = :developer")
    Double sumProductivityByDeveloper(
            @Param("developer") Developer developer);

    // ── Average productivity score for a developer ────────────────────────────
    @Query("SELECT AVG(p.productivityScore) " +
           "FROM ProductivityMetric p " +
           "WHERE p.developer = :developer")
    Double avgProductivityByDeveloper(
            @Param("developer") Developer developer);

    // ── Count total metrics for a developer ──────────────────────────────────
    long countByDeveloper(Developer developer);

    // ── Find metrics within a date range ─────────────────────────────────────
    @Query("SELECT p FROM ProductivityMetric p " +
           "WHERE p.developer = :developer " +
           "AND p.metricDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.metricDate DESC")
    List<ProductivityMetric> findByDeveloperAndDateRange(
            @Param("developer")  Developer developer,
            @Param("startDate")  LocalDate startDate,
            @Param("endDate")    LocalDate endDate);
}