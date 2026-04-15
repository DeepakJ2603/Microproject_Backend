package com.example.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
            .defaultHeader(HttpHeaders.USER_AGENT, "DevDashboardApp")
            .filter((request, next) -> {
                System.out.println("Calling GitHub: " + request.url());
                System.out.println("Auth Header Present: " +
                    request.headers().containsKey(HttpHeaders.AUTHORIZATION));
                return next.exchange(request);
            })
            .build();
    }
}
