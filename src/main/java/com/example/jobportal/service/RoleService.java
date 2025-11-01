package com.example.jobportal.service;

import com.example.jobportal.data.entity.Permission;
import com.example.jobportal.data.entity.Role;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.RolePermissionRepository;
import com.example.jobportal.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;
    private final RolePermissionRepository rolePermissionRepo;

    public Optional<Role> getById(Long id) {
        return roleRepo.findById(id);
    }

    public Role create(Role role) {
        return roleRepo.create(role);
    }

    public Optional<Role> update(Long id, Role role) {
        return roleRepo.update(id, role);
    }

    public int delete(Long id) {
        rolePermissionRepo.deleteByRoleId(id);
        return roleRepo.delete(id);
    }

    public Page<Role> getAll(Role filter, Pageable pageable) {
        return roleRepo.findAll(filter, pageable);
    }

    /**
     * Gán permission cho role (thay thế toàn bộ danh sách)
     */
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        rolePermissionRepo.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long pid : permissionIds) {
                rolePermissionRepo.addPermissionToRole(roleId, pid);
            }
        }
    }

    public List<Long> getPermissionIdsOfRole(Long roleId) {
        return rolePermissionRepo.findByRoleId(roleId)
                .stream()
                .map(rp -> rp.getPermissionId())
                .toList();
    }

    /**
     * Lấy danh sách permissionId của role
     */
    public List<Permission> getPermissionsOfRole(Long roleId) {
        return rolePermissionRepo.findPermissionsByRoleId(roleId);
    }

    /**
     *  Thêm một quyền vào role
     */
    public void addPermissionToRole(Long roleId, Long permissionId) {
        rolePermissionRepo.addPermissionToRole(roleId, permissionId);
    }

    /**
     * Xóa một quyền khỏi role
     */
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        rolePermissionRepo.deletePermissionFromRole(roleId, permissionId);
    }

    /**
     * Kiểm tra role có quyền cụ thể hay không
     */
    public boolean hasPermission(Long roleId, Long permissionId) {
        return rolePermissionRepo.findByRoleId(roleId).stream()
                .anyMatch(rp -> rp.getPermissionId().equals(permissionId));
    }
}
