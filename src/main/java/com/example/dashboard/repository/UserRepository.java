package com.example.dashboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dashboard.entity.User;
import com.example.dashboard.model.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGithubId(String githubId);
    boolean existsByEmail(String email);
    List<User> findAllByRole(Role role);
}
