package com.example.dashboard.dto;

public class AuthResponse {

    private String token;
    private String role;
    private Long   userId;
    private String email;
    private String fullName;
    private String avatarUrl;

    public AuthResponse() {}

    public AuthResponse(String token,
                        String role,
                        Long   userId,
                        String email,
                        String fullName,
                        String avatarUrl) {
        this.token     = token;
        this.role      = role;
        this.userId    = userId;
        this.email     = email;
        this.fullName  = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getToken()              { return token; }
    public void   setToken(String t)      { this.token = t; }

    public String getRole()               { return role; }
    public void   setRole(String r)       { this.role = r; }

    public Long   getUserId()             { return userId; }
    public void   setUserId(Long u)       { this.userId = u; }

    public String getEmail()              { return email; }
    public void   setEmail(String e)      { this.email = e; }

    public String getFullName()           { return fullName; }
    public void   setFullName(String f)   { this.fullName = f; }

    public String getAvatarUrl()          { return avatarUrl; }
    public void   setAvatarUrl(String a)  { this.avatarUrl = a; }
}