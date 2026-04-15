package com.example.dashboard.service;

import com.example.dashboard.dto.DeveloperSummary;
import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.exception.ResourceNotFoundException;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ManagerRepository;
import com.example.dashboard.repository.ProductivityMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final DeveloperRepository          developerRepository;
    private final ManagerRepository            managerRepository;
    private final ProductivityMetricRepository metricRepository;

    public UserService(DeveloperRepository developerRepository,
                       ManagerRepository managerRepository,
                       ProductivityMetricRepository metricRepository) {
        this.developerRepository = developerRepository;
        this.managerRepository   = managerRepository;
        this.metricRepository    = metricRepository;
    }

    // ── Get all developers ────────────────────────────────────────────────────
    public List<DeveloperSummary> getAllDevelopers() {
        return developerRepository.findAll()
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // ── Get all managers ──────────────────────────────────────────────────────
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    // ── Get developer summary by ID ───────────────────────────────────────────
    public DeveloperSummary getDeveloperById(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Developer", "id", id));
        return toSummary(developer);
    }

    // ── Get developer entity by email ─────────────────────────────────────────
    public Developer getDeveloperByEmail(String email) {
        return developerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Developer", "email", email));
    }

    // ── Get manager entity by email ───────────────────────────────────────────
    public Manager getManagerByEmail(String email) {
        return managerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Manager", "email", email));
    }

    // ── Delete developer by ID ────────────────────────────────────────────────
    public void deleteDeveloper(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Developer", "id", id);
        }
        developerRepository.deleteById(id);
    }

    // ── Delete manager by ID ──────────────────────────────────────────────────
    public void deleteManager(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manager", "id", id);
        }
        managerRepository.deleteById(id);
    }

    // ── Developer Entity → DeveloperSummary DTO ───────────────────────────────
    public DeveloperSummary toSummary(Developer developer) {
        DeveloperSummary s = new DeveloperSummary();
        s.setId(developer.getId());
        s.setEmail(developer.getEmail());
        s.setFullName(developer.getFullName());
        s.setRole(developer.getRole().name());
        s.setAvatarUrl(developer.getAvatarUrl());
        s.setGithubUsername(developer.getGithubUsername());

        Double score = metricRepository
                .sumProductivityByDeveloper(developer);
        s.setProductivityScore(score != null ? score : 0.0);

        return s;
    }
}