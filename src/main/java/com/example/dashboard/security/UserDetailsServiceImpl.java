package com.example.dashboard.security;

import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Manager;
import com.example.dashboard.entity.User;
import com.example.dashboard.repository.DeveloperRepository;
import com.example.dashboard.repository.ManagerRepository;
import com.example.dashboard.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository      userRepository;
    private final ManagerRepository   managerRepository;
    private final DeveloperRepository developerRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  ManagerRepository managerRepository,
                                  DeveloperRepository developerRepository) {
        this.userRepository      = userRepository;
        this.managerRepository   = managerRepository;
        this.developerRepository = developerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // ── Step 1: Check User table (Super Admin) ────────────────────────────
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            return buildUserDetails(
                    u.getEmail(),
                    u.getPassword() != null ? u.getPassword() : "",
                    u.getRole().name()
            );
        }

        // ── Step 2: Check Manager table ───────────────────────────────────────
        var managerOpt = managerRepository.findByEmail(email);
        if (managerOpt.isPresent()) {
            Manager m = managerOpt.get();
            return buildUserDetails(
                    m.getEmail(),
                    m.getPassword(),
                    m.getRole().name()
            );
        }

        // ── Step 3: Check Developer table (GitHub OAuth) ──────────────────────
        var developerOpt = developerRepository.findByEmail(email);
        if (developerOpt.isPresent()) {
            Developer d = developerOpt.get();
            return buildUserDetails(
                    d.getEmail(),
                    "",               // developers use OAuth — no password
                    d.getRole().name()
            );
        }

        throw new UsernameNotFoundException(
                "No account found with email: " + email);
    }

    // ── Build Spring Security UserDetails object ──────────────────────────────
    private UserDetails buildUserDetails(String email,
                                         String password,
                                         String role) {
        return new org.springframework.security.core.userdetails.User(
                email,
                password,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}