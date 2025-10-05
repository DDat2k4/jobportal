package com.example.jobportal.service;

import com.example.jobportal.data.entity.Permission;
import com.example.jobportal.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    /**
     * Lấy danh sách permission của 1 role
     */
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return rolePermissionRepository.findPermissionsByRoleId(roleId);
    }

    /**
     * Thêm nhiều permission cho role (không xóa cũ)
     */
    @Transactional
    public void addPermissionsToRole(Long roleId, List<Long> permissionIds) {
        for (Long pid : permissionIds) {
            rolePermissionRepository.addPermissionToRole(roleId, pid);
        }
    }

    /**
     * Thay thế toàn bộ permission của role (xóa hết, thêm mới)
     */
    @Transactional
    public void replacePermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionRepository.deleteByRoleId(roleId);
        addPermissionsToRole(roleId, permissionIds);
    }

    /**
     * Xóa 1 permission cụ thể khỏi role
     */
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        rolePermissionRepository.deletePermissionFromRole(roleId, permissionId);
    }
}
