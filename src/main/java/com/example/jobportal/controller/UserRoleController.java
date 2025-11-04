package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Role;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService service;

    /**
     * Lấy danh sách role của user
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_ROLE_READ') and @userRoleSecurity.canViewOrModify(#userId)")
    public ApiResponse<List<Role>> getUserRoles(@PathVariable Long userId) {
        var roles = service.getRolesByUserId(userId);
        return ApiResponse.ok("User roles fetched successfully", roles);
    }

    /**
     * Thêm role cho user (chỉ ADMIN)
     */
    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_ROLE_CREATE') and @userRoleSecurity.canManageRoles(#userId)")
    public ApiResponse<Void> addRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        service.addRolesToUser(userId, roleIds);
        return ApiResponse.ok("Roles added successfully", null);
    }

    /**
     * Thay thế toàn bộ role của user (chỉ ADMIN)
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_ROLE_UPDATE') and @userRoleSecurity.canManageRoles(#userId)")
    public ApiResponse<Void> replaceUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        service.replaceUserRoles(userId, roleIds);
        return ApiResponse.ok("Roles replaced successfully", null);
    }

    /**
     * Xóa một role khỏi user (chỉ ADMIN)
     */
    @DeleteMapping("/{userId}/{roleId}")
    @PreAuthorize("hasAuthority('USER_ROLE_DELETE') and @userRoleSecurity.canManageRoles(#userId)")
    public ApiResponse<Void> removeUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        service.removeRoleFromUser(userId, roleId);
        return ApiResponse.ok("Role removed successfully", null);
    }
}
