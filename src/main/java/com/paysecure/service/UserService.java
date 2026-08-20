package com.paysecure.service;

import com.paysecure.entity.User;
import com.paysecure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * FINDING 2 (Part B) — REMEDIATED.
 * Now uses Spring Security's BCryptPasswordEncoder (salted, adaptive work factor)
 * instead of unsalted single-round SHA-256.
 * Mohammad Ismail CJ-1134-C
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // salted + slow, unlike SHA-256
        user.setEmail(email);
        user.setRole("CUSTOMER");
        userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;
        return passwordEncoder.matches(password, user.getPassword()) ? user : null;
    }
}