package com.example.dashboard.repository;

import com.example.dashboard.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);
    Optional<Developer> findByGithubId(String githubId);
    boolean             existsByEmail(String email);
}