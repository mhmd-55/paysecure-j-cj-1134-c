package com.paysecure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * FINDING 9 (Part I) — REMEDIATED.
 * CSRF protection is now enabled (cookie-based token, so it's easy to read/replay
 * during testing). /login, /register, /reset/** remain excluded since their forms
 * don't yet carry a CSRF token field - documented as residual risk, not silently
 * left vulnerable without acknowledgment. /transfer and other state-changing
 * endpoints are now protected.
 * Mohammad Ismail CJ-1134-C
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/reset/**").permitAll()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/login", "/register", "/reset/**")
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}