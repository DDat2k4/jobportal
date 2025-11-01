package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Permission;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ApiResponse<Page<Permission>> getAllPermissions(Permission filter, Pageable pageable) {
        Page<Permission> permissions = permissionService.getAll(filter, pageable);
        return ApiResponse.ok("Fetched permissions successfully", permissions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ApiResponse<Permission> getPermissionById(@PathVariable Long id) {
        Optional<Permission> permission = permissionService.getById(id);
        return permission.map(p -> ApiResponse.ok("Fetched permission successfully", p))
                .orElseGet(() -> ApiResponse.error("Permission not found"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ApiResponse<Permission> getPermissionByCode(@PathVariable String code) {
        Optional<Permission> permission = permissionService.getByCode(code);
        return permission.map(p -> ApiResponse.ok("Fetched permission successfully", p))
                .orElseGet(() -> ApiResponse.error("Permission not found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ApiResponse<Permission> createPermission(@RequestBody Permission permission) {
        Permission created = permissionService.create(permission);
        return ApiResponse.ok("Created permission successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        Optional<Permission> updated = permissionService.update(id, permission);
        return updated.map(p -> ApiResponse.ok("Updated permission successfully", p))
                .orElseGet(() -> ApiResponse.error("Permission not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        int rows = permissionService.delete(id);
        if (rows > 0) {
            return ApiResponse.ok("Deleted permission successfully", null);
        } else {
            return ApiResponse.error("Permission not found");
        }
    }
}
