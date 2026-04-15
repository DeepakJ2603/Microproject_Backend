package com.example.dashboard.service;

import com.example.dashboard.dto.DeveloperSummary;
import com.example.dashboard.dto.TeamRequest;
import com.example.dashboard.dto.TeamResponse;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.entity.Team;
import com.example.dashboard.entity.TeamMember;
import com.example.dashboard.exception.BadRequestException;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ProductivityMetricRepository;
import com.example.dashboard.repository.TeamMemberRepository;
import com.example.dashboard.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository               teamRepository;
    private final TeamMemberRepository         teamMemberRepository;
    private final DeveloperRepository          developerRepository;
    private final ProductivityMetricRepository metricRepository;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       DeveloperRepository developerRepository,
                       ProductivityMetricRepository metricRepository) {
        this.teamRepository       = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.developerRepository  = developerRepository;
        this.metricRepository     = metricRepository;
    }

    // ── Create Team ───────────────────────────────────────────────────────────
    @Transactional
    public TeamResponse createTeam(TeamRequest request, Manager manager) {
        // Guard: duplicate team name under same manager
        if (teamRepository.existsByNameAndManager(
                request.getName(), manager)) {
            throw new BadRequestException(
                    "Team name '" + request.getName()
                    + "' already exists for this manager");
        }

        Team team = new Team(request.getName(), manager);
        team.setDescription(request.getDescription());
        team = teamRepository.save(team);

        if (request.getMemberIds() != null) {
            for (Long devId : request.getMemberIds()) {
                Developer dev = developerRepository.findById(devId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Developer", "id", devId));
                if (!teamMemberRepository.existsByTeamAndDeveloper(
                        team, dev)) {
                    teamMemberRepository.save(new TeamMember(team, dev));
                }
            }
        }
        return toResponse(team);
    }

    // ── Update Team ───────────────────────────────────────────────────────────
    @Transactional
    public TeamResponse updateTeam(Long id, TeamRequest request,
                                   Manager manager) {
        Team team = getTeamEntity(id);
        assertManager(team, manager);

        // Guard: duplicate name check (ignore same team)
        if (!team.getName().equals(request.getName())
                && teamRepository.existsByNameAndManager(
                        request.getName(), manager)) {
            throw new BadRequestException(
                    "Team name '" + request.getName()
                    + "' already exists for this manager");
        }

        team.setName(request.getName());
        team.setDescription(request.getDescription());
        teamRepository.save(team);

        if (request.getMemberIds() != null) {
            // Remove members not in new list
            List<TeamMember> existing =
                    teamMemberRepository.findByTeam(team);
            for (TeamMember tm : existing) {
                if (!request.getMemberIds().contains(
                        tm.getDeveloper().getId())) {
                    teamMemberRepository.delete(tm);
                }
            }
            // Add new members
            for (Long devId : request.getMemberIds()) {
                Developer dev = developerRepository.findById(devId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Developer", "id", devId));
                if (!teamMemberRepository.existsByTeamAndDeveloper(
                        team, dev)) {
                    teamMemberRepository.save(new TeamMember(team, dev));
                }
            }
        }
        return toResponse(team);
    }

    // ── Delete Team ───────────────────────────────────────────────────────────
    @Transactional
    public void deleteTeam(Long id, Manager manager) {
        Team team = getTeamEntity(id);
        assertManager(team, manager);
        teamMemberRepository.deleteAllByTeam(team);  // remove members first
        teamRepository.delete(team);
    }

    // ── Get Teams By Manager ──────────────────────────────────────────────────
    public List<TeamResponse> getTeamsByManager(Manager manager) {
        return teamRepository
                .findByManagerOrderByCreatedAtDesc(manager)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get All Teams ─────────────────────────────────────────────────────────
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get Team By ID ────────────────────────────────────────────────────────
    public TeamResponse getTeamById(Long id) {
        return toResponse(getTeamEntity(id));
    }

    // ── Add Member ────────────────────────────────────────────────────────────
    @Transactional
    public TeamResponse addMember(Long teamId, Long devId,
                                  Manager manager) {
        Team      team = getTeamEntity(teamId);
        assertManager(team, manager);
        Developer dev  = developerRepository.findById(devId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Developer", "id", devId));
        if (!teamMemberRepository.existsByTeamAndDeveloper(team, dev)) {
            teamMemberRepository.save(new TeamMember(team, dev));
        }
        return toResponse(team);
    }

    // ── Remove Member ─────────────────────────────────────────────────────────
    @Transactional
    public TeamResponse removeMember(Long teamId, Long devId,
                                     Manager manager) {
        Team      team = getTeamEntity(teamId);
        assertManager(team, manager);
        Developer dev  = developerRepository.findById(devId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Developer", "id", devId));
        teamMemberRepository.findByTeamAndDeveloper(team, dev)
                .ifPresent(teamMemberRepository::delete);
        return toResponse(team);
    }

    // ── Get member count for a team ───────────────────────────────────────────
    public long getMemberCount(Long teamId) {
        Team team = getTeamEntity(teamId);
        return teamMemberRepository.countByTeam(team);
    }

    // ── Get teams a developer belongs to ─────────────────────────────────────
    public List<TeamResponse> getTeamsForDeveloper(Developer developer) {
        return teamMemberRepository.findByDeveloper(developer)
                .stream()
                .map(tm -> toResponse(tm.getTeam()))
                .collect(Collectors.toList());
    }

    // ── Private Helpers ───────────────────────────────────────────────────────
    private Team getTeamEntity(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Team", "id", id));
    }

    private void assertManager(Team team, Manager manager) {
        if (!team.getManager().getId().equals(manager.getId())) {
            throw new BadRequestException(
                    "You are not authorized to modify this team");
        }
    }

    // ── Entity → DTO ──────────────────────────────────────────────────────────
    private TeamResponse toResponse(Team team) {
        TeamResponse resp = new TeamResponse();
        resp.setId(team.getId());
        resp.setName(team.getName());
        resp.setDescription(team.getDescription());
        resp.setCreatedAt(team.getCreatedAt());
        resp.setManagerId(team.getManager().getId());
        resp.setManagerName(team.getManager().getFullName());

        List<TeamMember> members = teamMemberRepository.findByTeam(team);

        List<DeveloperSummary> summaries = members.stream().map(tm -> {
            Developer        d = tm.getDeveloper();
            DeveloperSummary s = new DeveloperSummary();
            s.setId(d.getId());
            s.setEmail(d.getEmail());
            s.setFullName(d.getFullName());
            s.setRole(d.getRole().name());
            s.setAvatarUrl(d.getAvatarUrl());
            s.setGithubUsername(d.getGithubUsername());
            Double score = metricRepository.sumProductivityByDeveloper(d);
            s.setProductivityScore(score != null ? score : 0.0);
            return s;
        }).collect(Collectors.toList());

        double total = summaries.stream()
                .mapToDouble(s -> s.getProductivityScore() != null
                        ? s.getProductivityScore() : 0.0)
                .sum();

        resp.setMembers(summaries);
        resp.setTotalProductivityScore(BigDecimal.valueOf(total));
        return resp;
    }
}