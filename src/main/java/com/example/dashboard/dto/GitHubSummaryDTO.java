package com.example.dashboard.dto;

public class GitHubSummaryDTO {

    private int totalCommits;
    private int totalPRs;
    private int totalIssues;
    private double latestScore;

    public GitHubSummaryDTO(int totalCommits, int totalPRs,
                            int totalIssues, double latestScore) {
        this.totalCommits = totalCommits;
        this.totalPRs = totalPRs;
        this.totalIssues = totalIssues;
        this.latestScore = latestScore;
    }

    public int getTotalCommits() { return totalCommits; }
    public int getTotalPRs() { return totalPRs; }
    public int getTotalIssues() { return totalIssues; }
    public double getLatestScore() { return latestScore; }
}