package com.paysecure.service;

import com.paysecure.entity.User;
import com.paysecure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * FINDING 2 (Part B): unsalted SHA-256 password hashing.
 * Developer comment under test: "SHA-256 is an industry-standard cryptographic
 * algorithm, so no additional password protection is necessary."
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(String username, String password, String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] passwordHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String storedPassword = Base64.getEncoder().encodeToString(passwordHash);

            User user = new User();
            user.setUsername(username);
            user.setPassword(storedPassword);
            user.setEmail(email);
            user.setRole("CUSTOMER");
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Matches the same (weak) hashing scheme so login can verify what registerUser stored. */
    public User authenticate(String username, String password) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) return null;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] passwordHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String attempt = Base64.getEncoder().encodeToString(passwordHash);
            return attempt.equals(user.getPassword()) ? user : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
