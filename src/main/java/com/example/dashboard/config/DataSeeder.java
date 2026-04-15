package com.example.dashboard.config;

import com.example.dashboard.entity.User;
import com.example.dashboard.model.Role;
import com.example.dashboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.super-admin.email}")
    private String superAdminEmail;

    @Value("${app.super-admin.password}")
    private String superAdminPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(superAdminEmail)) {
            User admin = new User();
            admin.setEmail(superAdminEmail);
            admin.setPassword(passwordEncoder.encode(superAdminPassword));
            admin.setRole(Role.ROLE_SUPER_ADMIN);
            admin.setFullName("Super Administrator");
            userRepository.save(admin);
            logger.info("✅ Super admin seeded successfully: {}", superAdminEmail);
        } else {
            logger.info("ℹ️  Super admin already exists: {}", superAdminEmail);
        }
    }
}