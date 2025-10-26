package com.example.jobportal.controller;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.UserService;
import com.example.jobportal.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Danh sách user (filter theo role)
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String role,
            Pageable pageable
    ) {
        var result = userService.getUsersByRole(role, pageable);
        return ApiResponse.ok("Result page UserDTO successful", result);
    }

    // Lấy thông tin user hiện tại
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<UserDTO> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("Unauthorized or invalid token");
        }
        var user = userService.getUserById(userId);
        return ApiResponse.ok("Fetched current user info", user);
    }

    // Lấy thông tin user cụ thể
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long userId) {
        var user = userService.getUserById(userId);
        return ApiResponse.ok("Fetched user info", user);
    }
}
