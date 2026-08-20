package com.paysecure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * FINDING 9 (Part I): CSRF disabled globally.
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/reset/**").permitAll()
                .anyRequest().permitAll() // kept permissive on purpose so every Part A-J endpoint is reachable for testing
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    /**
     * FINDING 2 (Part B) remediation: salted, adaptive password hashing.
     * Work factor 12 - a deliberate slow-down that resists GPU cracking, unlike raw SHA-256.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}