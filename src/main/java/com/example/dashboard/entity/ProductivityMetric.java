package com.example.dashboard.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "productivity_metric", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "developer_id", "metric_date" }) })
public class ProductivityMetric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "developer_id", nullable = false)
	private Developer developer;

	@Column(name = "metric_date", nullable = false)
	private LocalDate metricDate;

	@Column(nullable = false)
	private Integer commitsCount = 0;

	@Column(nullable = false)
	private Integer prsCount = 0;

	@Column(nullable = false)
	private Integer issuesCount = 0;

	@Column(nullable = false)
	private Double productivityScore = 0.0;

	public void recalculate() {
		this.productivityScore = (commitsCount * 1.0) + (prsCount * 2.0) + (issuesCount * 1.5);
	}

	public Long getId() {
		return id;
	}

	public Developer getDeveloper() {
		return developer;
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}

	public LocalDate getMetricDate() {
		return metricDate;
	}

	public void setMetricDate(LocalDate metricDate) {
		this.metricDate = metricDate;
	}

	public Integer getCommitsCount() {
		return commitsCount;
	}

	public void setCommitsCount(Integer commitsCount) {
		this.commitsCount = commitsCount;
	}

	public Integer getPrsCount() {
		return prsCount;
	}

	public void setPrsCount(Integer prsCount) {
		this.prsCount = prsCount;
	}

	public Integer getIssuesCount() {
		return issuesCount;
	}

	public void setIssuesCount(Integer issuesCount) {
		this.issuesCount = issuesCount;
	}

	public Double getProductivityScore() {
		return productivityScore;
	}
}
