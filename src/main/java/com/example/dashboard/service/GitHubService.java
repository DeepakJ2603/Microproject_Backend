package com.example.dashboard.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

@Service
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    private final WebClient webClient;

    public GitHubService(WebClient webClient) {
        this.webClient = webClient;
    }

    // ─────────────────────────────────────────────
    // Authenticated client
    // ─────────────────────────────────────────────
    private WebClient buildClient(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("GitHub access token missing");
        }

        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .defaultHeader("User-Agent", "DevDashboardApp")
                .defaultHeader("Authorization", "Bearer " + accessToken.trim())
                .build();
    }

    // ─────────────────────────────────────────────
    // Public profile (username‑based)
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserProfile(String username) {
        try {
            return webClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            logger.warn("GitHub profile fetch failed for {}", username);
            return Collections.emptyMap();
        }
    }

    // ─────────────────────────────────────────────
    // All repos (public + private)
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserReposAll(String accessToken) {
        return buildClient(accessToken)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("per_page", 100)
                        .build())
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }

    // ─────────────────────────────────────────────
    // Public events (username‑based)
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserEvents(String username) {
        try {
            return webClient.get()
                    .uri("/users/{username}/events/public?per_page=30", username)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ─────────────────────────────────────────────
    // Get all branches of a repo (SAFE)
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getBranches(
            String owner,
            String repo,
            String accessToken
    ) {

        try {
            return buildClient(accessToken)
                    .get()
                    .uri("/repos/{owner}/{repo}/branches", owner, repo)
                    .exchangeToMono(response -> {

                        if (response.statusCode().is2xxSuccessful()) {
                            return response.bodyToMono(List.class);
                        }

                        if (response.statusCode().value() == 404) {
                            logger.warn(
                                    "GitHub repo not accessible (404): {}/{}",
                                    owner, repo
                            );
                            return Mono.just(Collections.emptyList());
                        }

                        return response.createException().flatMap(Mono::error);
                    })
                    .block();

        } catch (Exception e) {
            logger.error("Failed to fetch branches for {}/{}", owner, repo, e);
            return Collections.emptyList();
        }
    }

    // ─────────────────────────────────────────────
    // ✅ TOTAL commits across ALL branches (FIXED)
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public int countAllCommitsAcrossAllBranches(
            String owner,
            String repo,
            String accessToken
    ) {

        Set<String> uniqueCommitShas = new HashSet<>();

        List<Map<String, Object>> branches =
                getBranches(owner, repo, accessToken);

        if (branches == null || branches.isEmpty()) {
            return 0;
        }

        for (Map<String, Object> branch : branches) {
            String branchName = (String) branch.get("name");
            int page = 1;

            while (true) {

                // ✅ FIX: lambda-safe variable
                final int currentPage = page;

                List<Map<String, Object>> commits = buildClient(accessToken)
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/repos/{owner}/{repo}/commits")
                                .queryParam("sha", branchName)
                                .queryParam("per_page", 100)
                                .queryParam("page", currentPage)
                                .build(owner, repo))
                        .retrieve()
                        .bodyToMono(List.class)   // ✅ convert FIRST
                        .onErrorReturn(
                                WebClientResponseException.class,
                                Collections.emptyList()
                        )
                        .block();

                if (commits == null || commits.isEmpty()) {
                    break;
                }

                for (Map<String, Object> commit : commits) {
                    uniqueCommitShas.add((String) commit.get("sha"));
                }

                page++; // ✅ safe mutation
            }
        }

        return uniqueCommitShas.size();
    }

    // ─────────────────────────────────────────────
    // ✅ TOTAL commits across ALL repos
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public int getTotalCommitCountAcrossAllRepos(
            String username,
            String accessToken
    ) {

        int totalCommits = 0;

        for (Map<String, Object> repo : getUserReposAll(accessToken)) {
            String repoName = (String) repo.get("name");
            Map<String, Object> ownerMap = (Map<String, Object>) repo.get("owner");
            String ownerLogin = (String) ownerMap.get("login");

            totalCommits += countAllCommitsAcrossAllBranches(
                    ownerLogin,
                    repoName,
                    accessToken
            );
        }

        return totalCommits;
    }

    // ─────────────────────────────────────────────
    // Authenticated profile
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAuthenticatedUserProfile(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            Map<String, Object> profile = buildClient(accessToken)
                    .get()
                    .uri("/user")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return profile != null ? profile : Collections.emptyMap();

        } catch (WebClientResponseException e) {
            logger.warn("Failed to fetch authenticated GitHub profile: {}", e.getMessage());
            return Collections.emptyMap();

        } catch (Exception e) {
            logger.error("Unexpected error fetching authenticated GitHub profile", e);
            return Collections.emptyMap();
        }
    }
}