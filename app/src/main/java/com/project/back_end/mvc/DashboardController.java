package com.project.back_end.mvc;

import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    // Autowire the Shared Service:
    // Inject the common Service class that provides token validation logic.
    @Autowired
    private Service service;

    // Admin Dashboard Controller
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        // Validate token for admin role
        Map<String, String> validation = service.validateToken(token, "admin");

        // If validation map is empty → token is valid
        if (validation.isEmpty()) {
            return "admin/adminDashboard";   // Thymeleaf template
        }

        // Invalid token → redirect to login page
        return "redirect:/";
    }

    // Doctor Dashboard Controller
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        // Validate token for doctor role
        Map<String, String> validation = service.validateToken(token, "doctor");

        if (validation.isEmpty()) {
            return "doctor/doctorDashboard";  // Thymeleaf template
        }

        // Invalid token → redirect to login page
        return "redirect:/";
    }
}
