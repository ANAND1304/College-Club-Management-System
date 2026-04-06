package com.clubmanagement.service;

import com.clubmanagement.dto.AuthDTO;
import com.clubmanagement.model.User;
import com.clubmanagement.repository.UserRepository;
import com.clubmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .department(request.getDepartment())
            .phone(request.getPhone())
            .role(User.Role.STUDENT)
            .active(true)
            .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthDTO.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthDTO.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
