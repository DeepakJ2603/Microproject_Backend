package com.example.dashboard.scheduler;

import com.example.dashboard.entity.Developer;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.service.GitHubMetricService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitHubMetricScheduler {

    private final DeveloperRepository developerRepository;
    private final GitHubMetricService gitHubMetricService;

    public GitHubMetricScheduler(DeveloperRepository developerRepository,
                                 GitHubMetricService gitHubMetricService) {
        this.developerRepository = developerRepository;
        this.gitHubMetricService = gitHubMetricService;
    }

    @Scheduled(cron = "0 30 23 * * ?")
    public void syncAllDevelopers() {

        List<Developer> developers = developerRepository.findAll();

        for (Developer developer : developers) {
            gitHubMetricService.syncTodayMetrics(developer);
        }
    }
}
