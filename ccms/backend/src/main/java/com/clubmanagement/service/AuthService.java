package com.clubmanagement.service;

import com.clubmanagement.dto.AuthDTO;
import com.clubmanagement.model.User;
import com.clubmanagement.repository.UserRepository;
import com.clubmanagement.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.admin-secret:CLUBHUB_ADMIN_2024}")
    private String adminSecret;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository        = userRepository;
        this.passwordEncoder       = passwordEncoder;
        this.jwtService            = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        User.Role role = User.Role.STUDENT;
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            if (request.getAdminSecret() == null || request.getAdminSecret().isBlank()) {
                throw new RuntimeException("Admin secret key is required");
            }
            if (!adminSecret.equals(request.getAdminSecret())) {
                throw new RuntimeException("Invalid admin secret key");
            }
            role = User.Role.ADMIN;
        }

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .department(request.getDepartment())
            .phone(request.getPhone())
            .role(role)
            .active(true)
            .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthDTO.AuthResponse(
            token, user.getId(), user.getName(), user.getEmail(), user.getRole()
        );
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthDTO.AuthResponse(
            token, user.getId(), user.getName(), user.getEmail(), user.getRole()
        );
    }
}
