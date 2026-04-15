package com.example.dashboard.repository;

import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.ProductivityMetric;
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

        List<ProductivityMetric> findByDeveloperOrderByMetricDateDesc(
                        Developer developer);

        Optional<ProductivityMetric> findByDeveloperAndMetricDate(
                        Developer developer, LocalDate metricDate);

        @Query("SELECT SUM(p.productivityScore) " +
                        "FROM ProductivityMetric p " +
                        "WHERE p.developer = :developer")
        Double sumProductivityByDeveloper(
                        @Param("developer") Developer developer);

        @Query("SELECT AVG(p.productivityScore) " +
                        "FROM ProductivityMetric p " +
                        "WHERE p.developer = :developer")
        Double avgProductivityByDeveloper(
                        @Param("developer") Developer developer);

        long countByDeveloper(Developer developer);

        @Query("SELECT p FROM ProductivityMetric p " +
                        "WHERE p.developer = :developer " +
                        "AND p.metricDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY p.metricDate DESC")
        List<ProductivityMetric> findByDeveloperAndDateRange(
                        @Param("developer") Developer developer,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}