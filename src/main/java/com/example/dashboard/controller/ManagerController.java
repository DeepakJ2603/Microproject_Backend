package com.example.dashboard.controller;

import com.example.dashboard.dto.ApiResponse;
import com.example.dashboard.dto.TeamRequest;
import com.example.dashboard.dto.TeamResponse;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ManagerRepository;
import com.example.dashboard.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','MANAGER')")
public class ManagerController {

    private final TeamService         teamService;
    private final ManagerRepository   managerRepository;
    private final DeveloperRepository developerRepository;

    public ManagerController(TeamService teamService,
                              ManagerRepository managerRepository,
                              DeveloperRepository developerRepository) {
        this.teamService        = teamService;
        this.managerRepository  = managerRepository;
        this.developerRepository = developerRepository;
    }

    @PostMapping("/teams")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @Valid @RequestBody TeamRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok("Team created",
                teamService.createTeam(request, getManager(ud))));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok("Team updated",
                teamService.updateTeam(id, request, getManager(ud))));
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        teamService.deleteTeam(id, getManager(ud));
        return ResponseEntity.ok(ApiResponse.ok("Team deleted", null));
    }

    @GetMapping("/teams")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getMyTeams(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok("Teams retrieved",
                teamService.getTeamsByManager(getManager(ud))));
    }

    @GetMapping("/teams/all")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getAllTeams() {
        return ResponseEntity.ok(ApiResponse.ok("All teams retrieved",
                teamService.getAllTeams()));
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Team retrieved",
                teamService.getTeamById(id)));
    }

    @PostMapping("/teams/{teamId}/members/{devId}")
    public ResponseEntity<ApiResponse<TeamResponse>> addMember(
            @PathVariable Long teamId,
            @PathVariable Long devId,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok("Member added",
                teamService.addMember(teamId, devId, getManager(ud))));
    }

    @DeleteMapping("/teams/{teamId}/members/{devId}")
    public ResponseEntity<ApiResponse<TeamResponse>> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long devId,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok("Member removed",
                teamService.removeMember(teamId, devId, getManager(ud))));
    }

    @GetMapping("/developers")
    public ResponseEntity<ApiResponse<?>> getDevelopers() {
        return ResponseEntity.ok(ApiResponse.ok("Developers retrieved",
                developerRepository.findAll()));
    }

    @GetMapping("/teams/{id}/members/count")
    public ResponseEntity<ApiResponse<Long>> getMemberCount(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Member count retrieved",
                teamService.getMemberCount(id)));
    }

    private Manager getManager(UserDetails ud) {
        return managerRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Manager", "email", ud.getUsername()));
    }
}