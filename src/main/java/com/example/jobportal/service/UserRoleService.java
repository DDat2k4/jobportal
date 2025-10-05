package com.example.jobportal.service;

import com.example.jobportal.data.entity.Role;
import com.example.jobportal.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        userRoleRepository.deleteByUserId(userId);
        addRolesToUser(userId, roleIds);
    }

    /**
     * Xóa 1 role cụ thể khỏi user
     */
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        userRoleRepository.deleteRoleFromUser(userId, roleId);
    }
}
