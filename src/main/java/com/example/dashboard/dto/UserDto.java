package com.example.dashboard.dto;

import com.example.dashboard.model.Role;

public class UserDto {

    private Long id;
    private String email;
    private String githubId;
    private Role role;

    public UserDto() {}

    public UserDto(Long id, String email, String githubId, Role role) {
        this.id = id;
        this.email = email;
        this.githubId = githubId;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}