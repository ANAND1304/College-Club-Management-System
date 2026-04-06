package com.clubmanagement.config;

import com.clubmanagement.model.User;
import com.clubmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a default admin user on first startup if none exists.
 * Credentials: admin@college.com / password
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@college.com")) {
            User admin = User.builder()
                .name("System Admin")
                .email("admin@college.com")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.ADMIN)
                .department("Administration")
                .active(true)
                .build();
            userRepository.save(admin);
            log.info("✅  Default admin created → admin@college.com / password");
        } else {
            log.info("ℹ️  Admin already exists, skipping seed.");
        }
    }
}
