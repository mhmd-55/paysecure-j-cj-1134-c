package com.paysecure.controller;

import com.paysecure.entity.User;
import com.paysecure.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FINDING 3 (Part C) — REMEDIATED.
 * request.changeSessionId() issues a brand-new session ID at the moment of
 * authentication, so any session ID an attacker fixed beforehand becomes invalid.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User user = userService.authenticate(username, password);
        if (user != null) {
            HttpSession session = request.getSession(true); // ensure a session exists first
            request.changeSessionId();                       // then rotate its ID
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        userService.registerUser(username, password, email);
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/whoami")
    @org.springframework.web.bind.annotation.ResponseBody
    public String whoami(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "No session at all.";
        }
        Object username = session.getAttribute("username");
        return "Session ID: " + session.getId() + " | username attribute: " + username;
    }
}