package com.paysecure.controller;

import com.paysecure.entity.User;
import com.paysecure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

/**
 * FINDING 8 (Part H): weak java.util.Random, 6-digit token, echoed directly in the response body.
 */
@Controller
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/reset/request")
    @ResponseBody
    public String createResetToken(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        Random random = new Random();
        int value = random.nextInt(999999);
        String token = String.format("%06d", value);
        user.setResetToken(token);
        userRepository.save(user);
        return "Reset token created: " + token;
    }

    @GetMapping("/reset/password")
    @ResponseBody
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        // Intentionally minimal - matching handout scope (token-only lookup, no expiry/rate limit)
        return "If implemented: password would be reset for token " + token;
    }
}
