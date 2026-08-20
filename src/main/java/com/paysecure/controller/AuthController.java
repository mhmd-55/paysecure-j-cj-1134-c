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
 * FINDING 3 (Part C): session fixation. No session.invalidate()/regeneration after auth.
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
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = userService.authenticate(username, password);
        if (user != null) {
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

    /**
     * Diagnostic-only endpoint for testing Finding 3 (session fixation).
     * Echoes back whatever identity (if any) is currently attached to the
     * caller's session, so we can directly observe session state rather than
     * relying on a page like /dashboard that doesn't check authentication at all.
     * Safe to remove after testing - not part of the assignment's required functionality.
     */
    @GetMapping("/whoami")
    @org.springframework.web.bind.annotation.ResponseBody
    public String whoami(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false = don't create one, just check
        if (session == null) {
            return "No session at all.";
        }
        Object username = session.getAttribute("username");
        return "Session ID: " + session.getId() + " | username attribute: " + username;
    }
}