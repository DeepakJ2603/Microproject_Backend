package com.example.dashboard.controller;

import com.example.dashboard.dto.ApiResponse;
import com.example.dashboard.dto.RegisterManagerRequest;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ManagerRepository;
import com.example.dashboard.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AuthService         authService;
    private final ManagerRepository   managerRepository;
    private final DeveloperRepository developerRepository;

    public AdminController(AuthService authService,
                           ManagerRepository managerRepository,
                           DeveloperRepository developerRepository) {
        this.authService         = authService;
        this.managerRepository   = managerRepository;
        this.developerRepository = developerRepository;
    }

    // ── Create Manager ────────────────────────────────────────────────────────
    @PostMapping("/managers")
    public ResponseEntity<ApiResponse<Manager>> createManager(
            @Valid @RequestBody RegisterManagerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Manager created",
                authService.registerManager(request)));
    }

    // ── Update Manager ────────────────────────────────────────────────────────
    @PutMapping("/managers/{id}")
    public ResponseEntity<ApiResponse<Manager>> updateManager(
            @PathVariable Long id,
            @Valid @RequestBody RegisterManagerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Manager updated",
                authService.updateManager(id, request)));
    }

    // ── Delete Manager ────────────────────────────────────────────────────────
    @DeleteMapping("/managers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteManager(
            @PathVariable Long id) {
        authService.deleteManager(id);
        return ResponseEntity.ok(ApiResponse.ok("Manager deleted", null));
    }

    // ── Get All Managers ──────────────────────────────────────────────────────
    @GetMapping("/managers")
    public ResponseEntity<ApiResponse<List<Manager>>> getManagers() {
        return ResponseEntity.ok(ApiResponse.ok("Managers retrieved",
                managerRepository.findAll()));
    }

    // ── Get All Developers ────────────────────────────────────────────────────
    @GetMapping("/developers")
    public ResponseEntity<ApiResponse<List<Developer>>> getDevelopers() {
        return ResponseEntity.ok(ApiResponse.ok("Developers retrieved",
                developerRepository.findAll()));
    }

    // ── Delete Developer ──────────────────────────────────────────────────────
    @DeleteMapping("/developers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeveloper(
            @PathVariable Long id) {
        developerRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Developer deleted", null));
    }
}