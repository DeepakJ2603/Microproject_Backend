package com.example.dashboard.entity;
import java.time.LocalDateTime;

import com.example.dashboard.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "developers")
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String githubId;

    private String githubUsername;

    private String avatarUrl;

    /**
     * ✅ GitHub OAuth access token
     * Needed for calling GitHub APIs on behalf of this user
     */
    @Column(name = "github_access_token", length = 2000)
    private String accessToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_DEVELOPER;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── JPA lifecycle hooks ─────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Constructors ───────────────────────────────────────────────
    public Developer() {}

    public Developer(String fullName,
                     String email,
                     String githubId,
                     String githubUsername,
                     String avatarUrl) {
        this.fullName       = fullName;
        this.email          = email;
        this.githubId       = githubId;
        this.githubUsername = githubUsername;
        this.avatarUrl      = avatarUrl;
        this.role           = Role.ROLE_DEVELOPER;
    }

    // ── Getters & Setters ──────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGithubId() { return githubId; }
    public void setGithubId(String githubId) { this.githubId = githubId; }

    public String getGithubUsername() { return githubUsername; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    // ✅ Access token getters/setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
