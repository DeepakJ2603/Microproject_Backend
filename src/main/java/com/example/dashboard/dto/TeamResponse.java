package com.example.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TeamResponse {

    private Long                 id;
    private String               name;
    private String               description;
    private Long                 managerId;
    private String               managerName;
    private List<DeveloperSummary> members;
    private BigDecimal           totalProductivityScore;
    private LocalDateTime        createdAt;

    public TeamResponse() {}

    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }

    public String getName()                              { return name; }
    public void   setName(String n)                      { this.name = n; }

    public String getDescription()                       { return description; }
    public void   setDescription(String d)               { this.description = d; }

    public Long getManagerId()                           { return managerId; }
    public void setManagerId(Long m)                     { this.managerId = m; }

    public String getManagerName()                       { return managerName; }
    public void   setManagerName(String m)               { this.managerName = m; }

    public List<DeveloperSummary> getMembers()           { return members; }
    public void setMembers(List<DeveloperSummary> m)     { this.members = m; }

    public BigDecimal getTotalProductivityScore()        { return totalProductivityScore; }
    public void setTotalProductivityScore(BigDecimal s)  { this.totalProductivityScore = s; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
}