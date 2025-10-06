package com.example.jobportal.controller;

import com.example.jobportal.constant.RoleConstant;
import com.example.jobportal.data.request.AuthRequest;
import com.example.jobportal.data.request.RegisterRequest;
import com.example.jobportal.data.request.RefreshRequest;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.data.response.AuthResponse;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.AuthService;
import com.example.jobportal.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        // Kiểm tra username tồn tại
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Username already exists", null));
        }

        // Hash password
        String passwordHash = passwordEncoder.encode(request.getPassword());
        Long userId = userRepository.createUser(request.getUsername(), request.getEmail(), passwordHash);

        // Tạo profile mặc định
        userRepository.createProfile(userId, request.getUsername(), null);

        // Xác định role mặc định
        long roleId = RoleConstant.ROLE_EMPLOYER;

        // Lấy roleType từ request
        String requestedRole = request.getRoleType();

        // Chỉ chấp nhận role hợp lệ
        if (RoleConstant.ROLE_JOB_SEEKER == CommonUtils.toLong(requestedRole, 0L) ||
                RoleConstant.ROLE_EMPLOYER == CommonUtils.toLong(requestedRole, 0L)) {
            roleId = CommonUtils.toLong(requestedRole, RoleConstant.ROLE_EMPLOYER);
        }

        // Gán role cho user
        userRepository.assignRole(userId, roleId);


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
    @PostMapping("/logout-all/{userId}")
    public ResponseEntity<ApiResponse<Void>> logoutAll(@PathVariable Long userId) {
        authService.logoutAll(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out from all devices successfully!", null));
    }

    // CHANGE PASSWORD
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        String username = authentication.getName();
        authService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully. Please login again.", null));
    }
}
