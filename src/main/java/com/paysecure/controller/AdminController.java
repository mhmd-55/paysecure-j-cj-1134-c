package com.paysecure.controller;

import com.paysecure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FINDING 6 (Part F): "role" is trusted from the request instead of the security context.
 * Try: POST /admin/deleteUser?id=<target>&role=ADMIN as any logged-in customer.
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
    public String deleteUser(@RequestParam Long id, @RequestParam String role) {
        if ("ADMIN".equals(role)) {
            userRepository.deleteById(id);
        }
        return "redirect:/admin/users";
    }
}
