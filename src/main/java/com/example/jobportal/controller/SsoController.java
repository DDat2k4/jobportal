package com.example.jobportal.controller;

import com.example.jobportal.data.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class SsoController {

    @GetMapping("/sso/success")
    @PreAuthorize("hasAuthority('SSO_LOGIN')")
    public ApiResponse<Map<String, Object>> onSsoSuccess(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ApiResponse.error("Not authenticated");
        }

        return ApiResponse.ok("SSO login success", principal.getAttributes());
    }

    @GetMapping("/sso/failure")
    public ApiResponse<Void> onSsoFailure() {
        return ApiResponse.error("SSO login failed");
    }
}
