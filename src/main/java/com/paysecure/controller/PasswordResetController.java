package com.paysecure.controller;

import com.paysecure.entity.User;
import com.paysecure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

/**
 * FINDING 8 (Part H) — FULLY REMEDIATED.
 * - SecureRandom, 32-byte token (was java.util.Random, 6 digits).
 * - Only a SHA-256 digest of the token is ever stored (resetTokenHash) - the raw
 *   token exists only transiently in memory and in the simulated out-of-band
 *   delivery channel, never in the database and never in application logs.
 * - 15-minute expiry (resetTokenExpiry) and single-use enforcement (resetTokenUsed).
 * - The raw token is deliberately NEVER passed to log.info/log.error - it is
 *   bearer-equivalent to a password and must never appear in operational logs,
 *   only in the (simulated) email delivery itself.
 * - /reset/password is now a real, working endpoint: validates the token against
 *   the stored hash, rejects expired/reused/unknown tokens, and performs the
 *   actual password update via BCrypt on success.
 *   Mohammad Ismail CJ-1134-C
 */
@Controller
public class PasswordResetController {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_VALID_MINUTES = 15;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset/request")
    @ResponseBody
    public String createResetToken(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "If that account exists, a reset link has been sent.";
        }

        byte[] raw = new byte[32];
        secureRandom.nextBytes(raw);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        String tokenHash = sha256Hex(token);

        user.setResetTokenHash(tokenHash);
        user.setResetTokenExpiry(Instant.now().plus(TOKEN_VALID_MINUTES, ChronoUnit.MINUTES));
        user.setResetTokenUsed(false);
        userRepository.save(user);

        deliverOutOfBand(user.getUsername(), token);

        return "If that account exists, a reset link has been sent.";
    }

    @GetMapping("/reset/password")
    @ResponseBody
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        String tokenHash = sha256Hex(token);
        Optional<User> maybeUser = userRepository.findByResetTokenHash(tokenHash);

        if (maybeUser.isEmpty()) {
            return "Invalid or expired reset token.";
        }
        User user = maybeUser.get();

        if (user.isResetTokenUsed()) {
            return "This reset token has already been used.";
        }
        if (user.getResetTokenExpiry() == null || Instant.now().isAfter(user.getResetTokenExpiry())) {
            return "This reset token has expired.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetTokenUsed(true);
        userRepository.save(user);

        return "Password has been reset successfully.";
    }

    private void deliverOutOfBand(String username, String token) {
        try (FileWriter writer = new FileWriter("simulated-email-outbox.txt", true)) {
            writer.write("[" + Instant.now() + "] Reset link for " + username
                    + ": /reset/password?token=" + token + "\n");
        } catch (IOException e) {
            log.error("Failed to write simulated outbox entry for {} (token itself intentionally not logged)", username);
        }
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}