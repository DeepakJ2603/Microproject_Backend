package com.example.dashboard.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.dashboard.entity.Developer;
import com.example.dashboard.model.Role;
import com.example.dashboard.repository.DeveloperRepository;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final DeveloperRepository developerRepository;
    private final JwtUtils jwtUtils;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(
            DeveloperRepository developerRepository,
            JwtUtils jwtUtils,
            OAuth2AuthorizedClientService authorizedClientService) {

        this.developerRepository = developerRepository;
        this.jwtUtils = jwtUtils;
        this.authorizedClientService = authorizedClientService;
    }

    private String getAttr(OAuth2User user, String key) {
        Object value = user.getAttribute(key);
        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authToken =
                (OAuth2AuthenticationToken) authentication;

        OAuth2User oAuth2User = authToken.getPrincipal();

        // ── Profile info ───────────────────────────────
        String githubId       = getAttr(oAuth2User, "id");
        String githubUsername = getAttr(oAuth2User, "login");
        String avatarUrl      = getAttr(oAuth2User, "avatar_url");
        String fullName       = getAttr(oAuth2User, "name");
        String rawEmail       = getAttr(oAuth2User, "email");

        final String email =
                (rawEmail == null || rawEmail.isBlank())
                        ? githubUsername + "@github.com"
                        : rawEmail;

        // ✅ Correct way to load authorized client
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        authToken.getAuthorizedClientRegistrationId(),
                        authToken.getName()
                );

        String accessToken = null;
        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            accessToken = authorizedClient.getAccessToken().getTokenValue();
        }

        // ── Upsert Developer ───────────────────────────
        Developer developer = developerRepository
                .findByGithubId(githubId)
                .orElseGet(() -> {
                    Developer d = new Developer();
                    d.setGithubId(githubId);
                    d.setRole(Role.ROLE_DEVELOPER);
                    return d;
                });

        developer.setGithubUsername(githubUsername);
        developer.setEmail(email);
        developer.setFullName(fullName != null ? fullName : githubUsername);
        if (avatarUrl != null) developer.setAvatarUrl(avatarUrl);
        if (accessToken != null) developer.setAccessToken(accessToken);

        developerRepository.save(developer);

        // ── Generate App JWT ───────────────────────────
        String token = jwtUtils.generateToken(
                developer.getEmail(),
                developer.getRole().name(),
                developer.getId()
        );

        // ✅ IMPORTANT: use '&' not '&amp;'
        String redirectUrl =
                frontendUrl + "/oauth2/callback"
                        + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                        + "&role=" + developer.getRole().name()
                        + "&userId=" + developer.getId()
                        + "&email=" + URLEncoder.encode(developer.getEmail(), StandardCharsets.UTF_8)
                        + "&fullName=" + URLEncoder.encode(
                                developer.getFullName() != null ? developer.getFullName() : "",
                                StandardCharsets.UTF_8)
                        + "&avatarUrl=" + URLEncoder.encode(
                                developer.getAvatarUrl() != null ? developer.getAvatarUrl() : "",
                                StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
