package com.paysecure.controller;

import com.paysecure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * FINDING 6 (Part F) — REMEDIATED.
 * The caller's role is now read from their own session, never from a client-supplied
 * request parameter. The old "role" parameter is removed entirely - it should never
 * have been a trust source in the first place.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/users")
    public String usersPage() {
        return "admin-users";
    }

    @PostMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam Long id, HttpSession session) {
        Object sessionRole = session.getAttribute("role");
        if (!"ADMIN".equals(sessionRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin privileges required to delete a user.");
        }
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}