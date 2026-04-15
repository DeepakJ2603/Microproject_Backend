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

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            return buildUserDetails(
                    u.getEmail(),
                    u.getPassword() != null ? u.getPassword() : "",
                    u.getRole().name()
            );
        }

        var managerOpt = managerRepository.findByEmail(email);
        if (managerOpt.isPresent()) {
            Manager m = managerOpt.get();
            return buildUserDetails(
                    m.getEmail(),
                    m.getPassword(),
                    m.getRole().name()
            );
        }

        var developerOpt = developerRepository.findByEmail(email);
        if (developerOpt.isPresent()) {
            Developer d = developerOpt.get();
            return buildUserDetails(
                    d.getEmail(),
                    "",              
                    d.getRole().name()
            );
        }

        throw new UsernameNotFoundException(
                "No account found with email: " + email);
    }

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