package com.clubmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.clubmanagement.model.User;
import com.clubmanagement.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
