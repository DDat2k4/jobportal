package com.example.jobportal.controller;

import com.example.jobportal.constant.RoleConstant;
import com.example.jobportal.data.request.AuthRequest;
import com.example.jobportal.data.request.RefreshRequest;
import com.example.jobportal.data.request.RegisterRequest;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.data.response.AuthResponse;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.AuthService;
import com.example.jobportal.util.CommonUtils;
import com.example.jobportal.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Username already exists", null));
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        Long userId = userRepository.createUser(request.getUsername(), request.getEmail(), passwordHash);

        // Tạo profile mặc định
        userRepository.createProfile(userId, request.getUsername(), null);

        // Gán role
        long requestedRole = CommonUtils.toLong(request.getRoleType(), 0L);

        if (requestedRole != RoleConstant.ROLE_EMPLOYER &&
                requestedRole != RoleConstant.ROLE_JOB_SEEKER) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Invalid role type", null));
        }

        userRepository.assignRole(userId, requestedRole);

        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully!", null));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", authResponse));
    }

    // REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshRequest request) {
        AuthResponse authResponse = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", authResponse));
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully!", null));
    }

    // LOGOUT ALL DEVICES
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Unauthorized", null));
        }
        authService.logoutAll(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out from all devices successfully!", null));
    }

    // CHANGE PASSWORD
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();

        if (userId == null || username == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Unauthorized", null));
        }

        authService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully. Please login again.", null));
    }
}
