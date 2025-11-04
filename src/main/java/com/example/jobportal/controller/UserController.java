package com.example.jobportal.controller;

import com.example.jobportal.data.entity.User;
import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.UserService;
import com.example.jobportal.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- Lấy danh sách user theo role
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String role,
            Pageable pageable
    ) {
        var result = userService.getUsersByRole(role, pageable);
        return ApiResponse.ok("Fetched user list successfully", result);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER_READ') and @userSecurity.isAdmin()")
    public ApiResponse<Page<UserDTO>> searchUsers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Short active,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> roles,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        if (sortBy != null && !sortBy.isBlank()) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        UserDTO filter = new UserDTO()
                .setId(id)
                .setUsername(username)
                .setEmail(email)
                .setPhone(phone)
                .setActive(active)
                .setName(name)
                .setRoles(roles);

        Page<UserDTO> result = userService.getAll(filter, pageable);
        return ApiResponse.ok("Users fetched successfully", result);
    }

    // --- Lấy thông tin user hiện tại
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

    // --- Lấy thông tin user theo ID (chỉ admin hoặc chính chủ)
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ') and @userSecurity.canAccessUser(#userId)")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long userId) {
        var user = userService.getUserById(userId);
        return ApiResponse.ok("Fetched user info successfully", user);
    }

    // --- Lấy tất cả user (chỉ admin)
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER_READ') and @userSecurity.isAdmin()")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        var users = userService.getAllUsers();
        return ApiResponse.ok("Fetched all users successfully", users);
    }

    // --- Tạo user mới (chỉ admin)
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE') and @userSecurity.isAdmin()")
    public ApiResponse<Long> createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String passwordHash
    ) {
        Long userId = userService.createUser(username, email, passwordHash);
        return ApiResponse.ok("User created successfully", userId);
    }

    // --- Cập nhật user (admin hoặc chính chủ)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.canAccessUser(#id)")
    public ApiResponse<Integer> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String passwordHash,
            @RequestParam(defaultValue = "true") boolean active
    ) {
        short status = (short) (active ? 1 : 0);
        int updated = userService.updateUser(id, email, passwordHash, status);
        return ApiResponse.ok("User updated successfully", updated);
    }

    // --- Xóa user (chỉ admin)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE') and @userSecurity.isAdmin()")
    public ApiResponse<Integer> deleteUser(@PathVariable Long userId) {
        int deleted = userService.deleteUser(userId);
        return ApiResponse.ok("User deleted successfully", deleted);
    }

    // --- Kích hoạt user (chỉ admin)
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.isAdmin()")
    public ApiResponse<Integer> activateUser(@PathVariable Long userId) {
        int updated = userService.activateUser(userId);
        return ApiResponse.ok("User activated successfully", updated);
    }

    // --- Vô hiệu hóa user (chỉ admin)
    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.isAdmin()")
    public ApiResponse<Integer> deactivateUser(@PathVariable Long userId) {
        int updated = userService.deactivateUser(userId);
        return ApiResponse.ok("User deactivated successfully", updated);
    }

    // --- Khóa user (chỉ admin)
    @PostMapping("/{userId}/lock")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.isAdmin()")
    public ApiResponse<String> lockUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String until
    ) {
        LocalDateTime lockedUntil = until != null
                ? OffsetDateTime.parse(until).toLocalDateTime()
                : LocalDateTime.now().plusHours(1);

        userService.lockUser(userId, lockedUntil);
        return ApiResponse.ok("User locked until " + lockedUntil, null);
    }

    // --- Mở khóa user (chỉ admin)
    @PostMapping("/{userId}/unlock")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.isAdmin()")
    public ApiResponse<String> unlockUser(@PathVariable Long userId) {
        userService.unlockUser(userId);
        return ApiResponse.ok("User unlocked successfully", null);
    }

    // --- Reset số lần login sai (chỉ admin)
    @PostMapping("/{userId}/reset-attempts")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.isAdmin()")
    public ApiResponse<String> resetFailedAttempts(@PathVariable Long userId) {
        userService.resetFailedAttempts(userId);
        return ApiResponse.ok("Failed attempts reset successfully", null);
    }

    // --- Tăng số lần login sai (nội bộ hoặc admin)
    @PostMapping("/{userId}/increase-attempts")
    @PreAuthorize("hasAuthority('USER_UPDATE') and @userSecurity.isAdmin()")
    public ApiResponse<String> increaseFailedAttempts(@PathVariable Long userId) {
        userService.increaseFailedAttempts(userId);
        return ApiResponse.ok("Failed attempts increased successfully", null);
    }
}
