package com.paysecure.controller;

import com.paysecure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FINDING 5 (Part E): IDOR. Try /profile?userId=<any id other than your own>.
 */
@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String viewProfile(@RequestParam Long userId, Model model) {
        model.addAttribute("user", userRepository.findById(userId).orElseThrow());
        return "profile";
    }
}
