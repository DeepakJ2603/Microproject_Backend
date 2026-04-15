package com.example.dashboard.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dashboard.dto.ApiResponse;
import com.example.dashboard.dto.GitHubSummaryDTO;
import com.example.dashboard.dto.MetricRequest;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.ProductivityMetric;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.service.GitHubMetricService;
import com.example.dashboard.service.GitHubService;
import com.example.dashboard.service.ProductivityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/developer")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','MANAGER','DEVELOPER')")
public class DeveloperController {

	private final ProductivityService productivityService;
	private final GitHubService gitHubService;
	private final DeveloperRepository developerRepository;
	private final GitHubMetricService gitHubMetricService;

	public DeveloperController(ProductivityService productivityService, GitHubService gitHubService,
			DeveloperRepository developerRepository, GitHubMetricService gitHubMetricService) {
		super();
		this.productivityService = productivityService;
		this.gitHubService = gitHubService;
		this.developerRepository = developerRepository;
		this.gitHubMetricService = gitHubMetricService;
	}

	@PostMapping("/github/sync")
	public ResponseEntity<ApiResponse<String>> syncGitHubMetrics(@AuthenticationPrincipal UserDetails ud) {

		try {
			Developer developer = getDeveloper(ud);

			gitHubMetricService.syncTodayMetrics(developer);
			return ResponseEntity.ok(ApiResponse.ok("GitHub metrics synced successfully", null));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// ── Get all metrics ───────────────────────────────────────────────────────
	@GetMapping("/metrics")
	public ResponseEntity<ApiResponse<List<ProductivityMetric>>> getMetrics(@AuthenticationPrincipal UserDetails ud) {
		Developer dev = getDeveloper(ud);
		return ResponseEntity.ok(ApiResponse.ok("Metrics retrieved", productivityService.getMetricsForUser(dev)));
	}

	// ── Save / update metric ──────────────────────────────────────────────────
	@PostMapping("/metrics")
	public ResponseEntity<ApiResponse<ProductivityMetric>> saveMetric(@Valid @RequestBody MetricRequest request,
			@AuthenticationPrincipal UserDetails ud) {
		Developer dev = getDeveloper(ud);
		return ResponseEntity.ok(ApiResponse.ok("Metric saved", productivityService.upsertMetric(dev, request)));
	}

	// ── Delete metric ─────────────────────────────────────────────────────────
	@DeleteMapping("/metrics/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteMetric(@PathVariable Long id,
			@AuthenticationPrincipal UserDetails ud) {
		productivityService.deleteMetric(id, getDeveloper(ud));
		return ResponseEntity.ok(ApiResponse.ok("Metric deleted", null));
	}

	// ── Total score ───────────────────────────────────────────────────────────
	@GetMapping("/metrics/score")
	public ResponseEntity<ApiResponse<Double>> getTotalScore(@AuthenticationPrincipal UserDetails ud) {
		return ResponseEntity
				.ok(ApiResponse.ok("Score retrieved", productivityService.getTotalScoreForUser(getDeveloper(ud))));
	}

	@GetMapping("/metrics/summary")
	public ResponseEntity<ApiResponse<GitHubSummaryDTO>> getMetricsSummary(@AuthenticationPrincipal UserDetails ud) {

		Developer dev = getDeveloper(ud);

		return ResponseEntity
				.ok(ApiResponse.ok("GitHub summary retrieved", productivityService.getGitHubSummaryForDeveloper(dev)));
	}

	// ── Average score ─────────────────────────────────────────────────────────
	@GetMapping("/metrics/average")
	public ResponseEntity<ApiResponse<Double>> getAverageScore(@AuthenticationPrincipal UserDetails ud) {
		return ResponseEntity.ok(ApiResponse.ok("Average score retrieved",
				productivityService.getAverageScoreForUser(getDeveloper(ud))));
	}

	@GetMapping("/github/profile")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getGitHubProfile(
	        @AuthenticationPrincipal UserDetails ud) {

	    Developer dev = getDeveloper(ud);

	    if (dev.getAccessToken() == null) {
	        return ResponseEntity.ok(
	                ApiResponse.ok("No GitHub linked", Collections.emptyMap()));
	    }

	    return ResponseEntity.ok(
	            ApiResponse.ok(
	                    "Profile retrieved",
	                    gitHubService.getAuthenticatedUserProfile(
	                            dev.getAccessToken()
	                    )
	            )
	    );
	}


	// ── GitHub repos ──────────────────────────────────────────────────────────
	@GetMapping("/github/repos")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRepos(
	        @AuthenticationPrincipal UserDetails ud) {

	    Developer dev = getDeveloper(ud);

	    if (dev.getAccessToken() == null) {
	        return ResponseEntity.ok(
	                ApiResponse.ok("No GitHub linked", Collections.emptyList()));
	    }

	    return ResponseEntity.ok(
	            ApiResponse.ok(
	                    "Repos retrieved",
	                    gitHubService.getUserReposAll(
	                            dev.getAccessToken()
	                    )
	            )
	    );
	}


	// ── GitHub events ─────────────────────────────────────────────────────────
	@GetMapping("/github/events")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEvents(@AuthenticationPrincipal UserDetails ud) {
		Developer dev = getDeveloper(ud);
		if (dev.getGithubUsername() == null) {
			return ResponseEntity.ok(ApiResponse.ok("No GitHub linked", Collections.emptyList()));
		}
		return ResponseEntity
				.ok(ApiResponse.ok("Events retrieved", gitHubService.getUserEvents(dev.getGithubUsername())));
	}

	// ── Resolve Developer from JWT ────────────────────────────────────────────
	private Developer getDeveloper(UserDetails ud) {
		return developerRepository.findByEmail(ud.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Developer", "email", ud.getUsername()));
	}
}