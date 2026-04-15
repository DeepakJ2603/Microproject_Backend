package com.example.dashboard.service;

import com.example.dashboard.dto.AuthRequest;
import com.example.dashboard.dto.AuthResponse;
import com.example.dashboard.dto.RegisterManagerRequest;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.entity.User;
import com.example.dashboard.exception.BadRequestException;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ManagerRepository;
import com.example.dashboard.repository.UserRepository;
import com.example.dashboard.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final ManagerRepository     managerRepository;
    private final DeveloperRepository   developerRepository;
    private final JwtUtils              jwtUtils;
    private final PasswordEncoder       passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository        userRepository,
                       ManagerRepository     managerRepository,
                       DeveloperRepository   developerRepository,
                       JwtUtils              jwtUtils,
                       PasswordEncoder       passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository        = userRepository;
        this.managerRepository     = managerRepository;
        this.developerRepository   = developerRepository;
        this.jwtUtils              = jwtUtils;
        this.passwordEncoder       = passwordEncoder;
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    public AuthResponse login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Step 1: Super Admin
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            String tok = jwtUtils.generateToken(
                    u.getEmail(), u.getRole().name(), u.getId());
            return new AuthResponse(tok, u.getRole().name(),
                    u.getId(), u.getEmail(), u.getFullName(), u.getAvatarUrl());
        }

        // Step 2: Manager
        var managerOpt = managerRepository.findByEmail(request.getEmail());
        if (managerOpt.isPresent()) {
            Manager m = managerOpt.get();
            String tok = jwtUtils.generateToken(
                    m.getEmail(), m.getRole().name(), m.getId());
            return new AuthResponse(tok, m.getRole().name(),
                    m.getId(), m.getEmail(), m.getFullName(), null);
        }

        throw new ResourceNotFoundException(
                "Account", "email", request.getEmail());
    }

    // ── Register Manager (called by Super Admin) ──────────────────────────────
    public Manager registerManager(RegisterManagerRequest request) {
        if (managerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email is already in use: " + request.getEmail());
        }
        Manager manager = new Manager();
        manager.setFullName(request.getFullName());
        manager.setEmail(request.getEmail());
        manager.setPassword(passwordEncoder.encode(request.getPassword()));
        return managerRepository.save(manager);
    }

    // ── Update Manager ────────────────────────────────────────────────────────
    public Manager updateManager(Long id, RegisterManagerRequest request) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Manager", "id", id));

        // If email changed, check it's not taken by someone else
        if (!manager.getEmail().equals(request.getEmail())
                && managerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email is already in use: " + request.getEmail());
        }

        manager.setFullName(request.getFullName());
        manager.setEmail(request.getEmail());

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {
            manager.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return managerRepository.save(manager);
    }

    // ── Delete Manager ────────────────────────────────────────────────────────
    public void deleteManager(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manager", "id", id);
        }
        managerRepository.deleteById(id);
    }

    // ── Resolve Super Admin ───────────────────────────────────────────────────
    public User getSuperAdmin(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "email", email));
    }

    // ── Resolve Manager ───────────────────────────────────────────────────────
    public Manager getManager(String email) {
        return managerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Manager", "email", email));
    }

    // ── Resolve Developer ─────────────────────────────────────────────────────
    public Developer getDeveloper(String email) {
        return developerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Developer", "email", email));
    }
}