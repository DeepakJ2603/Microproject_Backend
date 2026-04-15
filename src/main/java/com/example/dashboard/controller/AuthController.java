package com.example.dashboard.controller;

import com.example.dashboard.dto.ApiResponse;
import com.example.dashboard.dto.AuthRequest;
import com.example.dashboard.dto.AuthResponse;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.entity.User;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ManagerRepository;
import com.example.dashboard.repository.UserRepository;
import com.example.dashboard.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService         authService;
    private final UserRepository      userRepository;
    private final ManagerRepository   managerRepository;
    private final DeveloperRepository developerRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          ManagerRepository managerRepository,
                          DeveloperRepository developerRepository) {
        this.authService         = authService;
        this.userRepository      = userRepository;
        this.managerRepository   = managerRepository;
        this.developerRepository = developerRepository;
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest request) {
        AuthResponse resp = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", resp));
    }

    // ── Get Current Logged-in User ────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        String role  = userDetails.getAuthorities()
                .iterator().next().getAuthority();

        // ── Super Admin ───────────────────────────────────────────────────────
        if (role.equals("ROLE_SUPER_ADMIN")) {
            User u = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User", "email", email));
            return ResponseEntity.ok(ApiResponse.ok("User info retrieved",
                    new AuthResponse(null, u.getRole().name(),
                            u.getId(), u.getEmail(),
                            u.getFullName(), u.getAvatarUrl())));
        }

        // ── Manager ───────────────────────────────────────────────────────────
        if (role.equals("ROLE_MANAGER")) {
            Manager m = managerRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager", "email", email));
            return ResponseEntity.ok(ApiResponse.ok("User info retrieved",
                    new AuthResponse(null, m.getRole().name(),
                            m.getId(), m.getEmail(),
                            m.getFullName(), null)));
        }

        // ── Developer ─────────────────────────────────────────────────────────
        Developer d = developerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Developer", "email", email));
        return ResponseEntity.ok(ApiResponse.ok("User info retrieved",
                new AuthResponse(null, d.getRole().name(),
                        d.getId(), d.getEmail(),
                        d.getFullName(), d.getAvatarUrl())));
    }
}