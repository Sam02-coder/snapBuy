package com.snapBuy.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.core.env.Environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.snapBuy.common.enums.Role;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.default-password}")
    private String adminDefaultPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin account already exists ({}), skipping seed.", adminEmail);
            return;
        }

        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminDefaultPassword))
                .role(Role.ADMIN)
                .active(true)
                .locked(false)
                .emailVerified(true) // admin skips OTP verification entirely
                .build();

        userRepository.save(admin);
        log.warn("Seeded Admin account ({}) with the default password from configuration. "
                + "Change it immediately in any non-local environment.", adminEmail);
    }
}