package com.example.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class TeamRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    private String description;

    private Set<Long> memberIds;

    // ── getters & setters ─────────────────────────────────────────────────────
    public String getName()                { return name; }
    public void   setName(String n)        { this.name = n; }

    public String getDescription()         { return description; }
    public void   setDescription(String d) { this.description = d; }

    public Set<Long> getMemberIds()              { return memberIds; }
    public void      setMemberIds(Set<Long> m)   { this.memberIds = m; }
}