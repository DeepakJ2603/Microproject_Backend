package com.example.dashboard.dto;

public class DeveloperSummary {

    private Long   id;
    private String email;
    private String fullName;
    private String role;
    private String avatarUrl;
    private String githubUsername;
    private Double productivityScore;

    public DeveloperSummary() {}

    public Long   getId()                           { return id; }
    public void   setId(Long id)                    { this.id = id; }

    public String getEmail()                        { return email; }
    public void   setEmail(String e)                { this.email = e; }

    public String getFullName()                     { return fullName; }
    public void   setFullName(String f)             { this.fullName = f; }

    public String getRole()                         { return role; }
    public void   setRole(String r)                 { this.role = r; }

    public String getAvatarUrl()                    { return avatarUrl; }
    public void   setAvatarUrl(String a)            { this.avatarUrl = a; }

    public String getGithubUsername()               { return githubUsername; }
    public void   setGithubUsername(String g)       { this.githubUsername = g; }

    public Double getProductivityScore()            { return productivityScore; }
    public void   setProductivityScore(Double p)    { this.productivityScore = p; }
}