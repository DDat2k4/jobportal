package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Role;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<Page<Role>> getAllRoles(Role filter, Pageable pageable) {
        Page<Role> roles = roleService.getAll(filter, pageable);
        return ApiResponse.ok("Fetched roles successfully", roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getById(id);
        return role.map(r -> ApiResponse.ok("Fetched role successfully", r))
                .orElseGet(() -> ApiResponse.error("Role not found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        Role created = roleService.create(role);
        return ApiResponse.ok("Created role successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Optional<Role> updated = roleService.update(id, role);
        return updated.map(r -> ApiResponse.ok("Updated role successfully", r))
                .orElseGet(() -> ApiResponse.error("Role not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        int rows = roleService.delete(id);
        if (rows > 0) {
            return ApiResponse.ok("Deleted role successfully", null);
        } else {
            return ApiResponse.error("Role not found");
        }
    }
}
