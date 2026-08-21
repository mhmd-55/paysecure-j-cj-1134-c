package com.paysecure.controller;

import com.paysecure.entity.User;
import com.paysecure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * FINDING 5 (Part E) — REMEDIATED.
 * The requested userId is now checked against the caller's own session identity.
 * Only the profile owner or an ADMIN may view a given profile.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String viewProfile(@RequestParam Long userId, HttpSession session, Model model) {
        Object sessionUserId = session.getAttribute("userId");
        Object sessionRole = session.getAttribute("role");

        if (sessionUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to view a profile.");
        }

        boolean isOwner = sessionUserId.equals(userId);
        boolean isAdmin = "ADMIN".equals(sessionRole);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view this profile.");
        }

        User user = userRepository.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }
}