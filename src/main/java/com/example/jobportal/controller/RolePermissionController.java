package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Permission;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_READ')")
    public ApiResponse<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        List<Permission> permissions = rolePermissionService.getPermissionsByRoleId(roleId);
        return ApiResponse.ok("Fetched permissions successfully", permissions);
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_CREATE')")
    public ApiResponse<Void> addPermissionsToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds
    ) {
        rolePermissionService.addPermissionsToRole(roleId, permissionIds);
        return ApiResponse.ok("Permissions added successfully", null);
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_UPDATE')")
    public ApiResponse<Void> replaceRolePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds
    ) {
        rolePermissionService.replacePermissions(roleId, permissionIds);
        return ApiResponse.ok("Permissions replaced successfully", null);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_DELETE')")
    public ApiResponse<Void> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId
    ) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ApiResponse.ok("Permission removed successfully", null);
    }
}
