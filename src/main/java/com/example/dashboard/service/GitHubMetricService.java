package com.example.dashboard.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.dashboard.dto.MetricRequest;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.exception.BadRequestException;

@Service
public class GitHubMetricService {

    private final GitHubService gitHubService;
    private final ProductivityService productivityService;

    public GitHubMetricService(
            GitHubService gitHubService,
            ProductivityService productivityService) {

        this.gitHubService = gitHubService;
        this.productivityService = productivityService;
    }

    // ── Sync metrics FROM GitHub for the logged-in developer ───────
    public void syncTodayMetrics(Developer developer) {

    	if (developer.getAccessToken() == null) {
    	    throw new BadRequestException("GitHub account not connected. Please login via GitHub again.");
    	}
    	System.out.println("Syncing GitHub metrics for: " + developer.getEmail());
    	System.out.println("GitHub username: " + developer.getGithubUsername());
    	System.out.println("Access token present: " + (developer.getAccessToken() != null));

        String username = developer.getGithubUsername();
        String token = developer.getAccessToken();

        int totalCommits = 0;

        List<Map<String, Object>> repos =
                gitHubService.getUserReposAll(token);

        for (Map<String, Object> repo : repos) {

//            // ✅ Skip forked repos (recommended)
//            Boolean fork = (Boolean) repo.get("fork");
//            if (Boolean.TRUE.equals(fork)) {
//                continue;
//            }

            String repoName = (String) repo.get("name");
            if (repoName == null) {
                continue;
            }

            totalCommits +=
                    gitHubService.countAllCommitsAcrossAllBranches(
                            username,
                            repoName,
                            token
                    );
        }

        // ✅ Save metric for today
        MetricRequest request = new MetricRequest();
        request.setCommitsCount(totalCommits);
        request.setPrsCount(0);      // add later
        request.setIssuesCount(0);   // add later

        
        productivityService.upsertMetric(developer, request);
    }
}
