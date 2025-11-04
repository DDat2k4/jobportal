package com.example.jobportal.service;

import com.example.jobportal.data.entity.Role;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    /**
     * Lấy danh sách role của user
     */
    public List<Role> getRolesByUserId(Long userId) {
        return userRoleRepository.findRolesByUserId(userId);
    }

    /**
     * Thêm nhiều role cho user (không xóa cũ)
     */
    @Transactional
    public void addRolesToUser(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            userRoleRepository.addRoleToUser(userId, roleId);
        }
    }

    /**
     * Thay thế toàn bộ role của user (xóa cũ, thêm mới)
     */
    @Transactional
    public void replaceUserRoles(Long userId, List<Long> roleIds) {
        userRoleRepository.deleteAllRolesByUserId(userId);
        addRolesToUser(userId, roleIds);
    }

    /**
     * Xóa 1 role cụ thể khỏi user
     */
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        userRoleRepository.deleteRoleFromUser(userId, roleId);
    }

    /**
     * Lấy danh sách permission code của user
     */
    public List<String> getPermissionsByUserId(Long userId) {
        return userRoleRepository.findPermissionsByUserId(userId).stream().toList();
    }

    /**
     * Lấy tất cả role (có phân trang)
     */
    public Page<Role> getAllRoles(Pageable pageable) {
        return userRoleRepository.findAllRoles(pageable);
    }

    /**
     * Lấy role theo ID
     */
    public Optional<Role> getRoleById(Long id) {
        return userRoleRepository.findRoleById(id);
    }

    /**
     * Tạo mới role
     */
    public Role createRole(Role role) {
        return userRoleRepository.createRole(role);
    }

    /**
     * Cập nhật role
     */
    public Optional<Role> updateRole(Long id, Role role) {
        return userRoleRepository.updateRole(id, role);
    }

    /**
     * Xóa role
     */
    public int deleteRole(Long id) {
        return userRoleRepository.deleteRole(id);
    }

    /**
     * Đếm tổng số role
     */
    public long countRoles() {
        return userRoleRepository.countRoles();
    }

    public boolean userHasRole(Long userId, Long roleId) {
        return userRoleRepository.existsUserRole(userId, roleId);
    }
}
