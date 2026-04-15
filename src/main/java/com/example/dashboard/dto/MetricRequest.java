package com.example.dashboard.dto;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public class MetricRequest {

    @Min(value = 0, message = "Commits count cannot be negative")
    private int commitsCount;

    @Min(value = 0, message = "PRs count cannot be negative")
    private int prsCount;

    @Min(value = 0, message = "Issues count cannot be negative")
    private int issuesCount;

    private LocalDate metricDate;  
    public int       getCommitsCount()          { return commitsCount; }
    public void      setCommitsCount(int c)     { this.commitsCount = c; }

    public int       getPrsCount()              { return prsCount; }
    public void      setPrsCount(int p)         { this.prsCount = p; }

    public int       getIssuesCount()           { return issuesCount; }
    public void      setIssuesCount(int i)      { this.issuesCount = i; }

    public LocalDate getMetricDate()            { return metricDate; }
    public void      setMetricDate(LocalDate d) { this.metricDate = d; }
}