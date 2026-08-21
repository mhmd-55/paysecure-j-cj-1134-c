package com.paysecure.controller;

import com.paysecure.entity.User;
import com.paysecure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * FINDING 8 (Part H) — REMEDIATED.
 * - SecureRandom replaces java.util.Random (cryptographically secure generation).
 * - A 32-byte token (far larger than a 6-digit space) replaces the 6-digit token.
 * - The token is NEVER returned in the HTTP response; it is only logged server-side,
 *   simulating out-of-band delivery (e.g. email) that a real attacker cannot observe.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class PasswordResetController {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

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

        user.setResetToken(token);
        userRepository.save(user);

        log.info("Password reset token generated for user '{}': {} (would be emailed, not returned in response)",
                username, token);

        return "If that account exists, a reset link has been sent.";
    }

    @GetMapping("/reset/password")
    @ResponseBody
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return "If implemented: password would be reset for token " + token;
    }
}