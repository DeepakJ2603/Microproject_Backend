package com.example.dashboard.entity;

import com.example.dashboard.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String fullName;

    private String avatarUrl;

    @Column(unique = true)
    private String githubId;

    private String githubUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public User() {}

    public User(String email, String password, Role role, String fullName) {
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.fullName = fullName;
    }

    public User(String email, String githubId, String password, Role role) {
        this.email    = email;
        this.githubId = githubId;
        this.password = password;
        this.role     = role;
    }

    public Long          getId()                      { return id; }
    public void          setId(Long id)               { this.id = id; }

    public String        getEmail()                   { return email; }
    public void          setEmail(String e)           { this.email = e; }

    public String        getPassword()                { return password; }
    public void          setPassword(String p)        { this.password = p; }

    public String        getFullName()                { return fullName; }
    public void          setFullName(String f)        { this.fullName = f; }

    public String        getAvatarUrl()               { return avatarUrl; }
    public void          setAvatarUrl(String a)       { this.avatarUrl = a; }

    public String        getGithubId()                { return githubId; }
    public void          setGithubId(String g)        { this.githubId = g; }

    public String        getGithubUsername()          { return githubUsername; }
    public void          setGithubUsername(String g)  { this.githubUsername = g; }

    public Role          getRole()                    { return role; }
    public void          setRole(Role r)              { this.role = r; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public LocalDateTime getUpdatedAt()               { return updatedAt; }
}