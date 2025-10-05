package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Role;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_ROLE_READ')")
    public ApiResponse<List<Role>> getUserRoles(@PathVariable Long userId) {
        List<Role> roles = userRoleService.getRolesByUserId(userId);
        return ApiResponse.ok("Fetched user roles successfully", roles);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_ROLE_CREATE')")
    public ApiResponse<Void> addRolesToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds
    ) {
        userRoleService.addRolesToUser(userId, roleIds);
        return ApiResponse.ok("Roles added successfully", null);
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_ROLE_UPDATE')")
    public ApiResponse<Void> replaceUserRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds
    ) {
        userRoleService.replaceUserRoles(userId, roleIds);
        return ApiResponse.ok("Roles replaced successfully", null);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('USER_ROLE_DELETE')")
    public ApiResponse<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId
    ) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ApiResponse.ok("Role removed successfully", null);
    }
}
