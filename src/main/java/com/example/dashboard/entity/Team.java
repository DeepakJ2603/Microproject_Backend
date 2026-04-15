package com.example.dashboard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Team name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @Column(name = "productivity_score",
            precision = 10, scale = 2)
    private BigDecimal productivityScore = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Team() {}

    public Team(String name, Manager manager) {
        this.name    = name;
        this.manager = manager;
    }

    public Team(String name, String description, Manager manager) {
        this.name        = name;
        this.description = description;
        this.manager     = manager;
    }

    public Long          getId()                            { return id; }
    public void          setId(Long id)                     { this.id = id; }

    public String        getName()                          { return name; }
    public void          setName(String n)                  { this.name = n; }

    public String        getDescription()                   { return description; }
    public void          setDescription(String d)           { this.description = d; }

    public Manager       getManager()                       { return manager; }
    public void          setManager(Manager m)              { this.manager = m; }

    public BigDecimal    getProductivityScore()             { return productivityScore; }
    public void          setProductivityScore(BigDecimal s) { this.productivityScore = s; }

    public LocalDateTime getCreatedAt()                     { return createdAt; }
    public void          setCreatedAt(LocalDateTime c)      { this.createdAt = c; }

    public LocalDateTime getUpdatedAt()                     { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime u)      { this.updatedAt = u; }
}