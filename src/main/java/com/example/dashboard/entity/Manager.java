package com.example.dashboard.entity;

import com.example.dashboard.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "managers")
public class Manager {

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

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_MANAGER;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // ── Constructors ──────────────────────────────────────────────────────────
    public Manager() {}

    public Manager(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.role     = Role.ROLE_MANAGER;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long          getId()               { return id; }
    public void          setId(Long id)        { this.id = id; }

    public String        getFullName()         { return fullName; }
    public void          setFullName(String f) { this.fullName = f; }

    public String        getEmail()            { return email; }
    public void          setEmail(String e)    { this.email = e; }

    public String        getPassword()         { return password; }
    public void          setPassword(String p) { this.password = p; }

    public Role          getRole()             { return role; }
    public void          setRole(Role r)       { this.role = r; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
}