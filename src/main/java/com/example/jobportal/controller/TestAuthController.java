package com.example.jobportal.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAuthController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "✅ Public endpoint: no auth required.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "✅ User endpoint: requires ROLE_USER.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "✅ Admin endpoint: requires ROLE_ADMIN.";
    }

    @GetMapping("/job-manage")
    @PreAuthorize("hasAuthority('job:manage')")
    public String manageJobsEndpoint() {
        return "✅ Permission endpoint: requires 'job:manage' permission.";
    }
}
