package com.example.jobportal.service;

import com.example.jobportal.constant.UserStatus;
import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Lấy thông tin user đầy đủ (profile + roles + permissions + tokens)
     */
    public Optional<UserDTO> getUserDetail(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Phân trang UserDTO theo role
     */
    public Page<UserDTO> getUsersByRole(String roleName, Pageable pageable) {
        return userRepository.findAllUsersByRole(roleName, pageable);
    }

    /**
     * Phân trang User theo điều kiện
     */
    public Page<UserDTO> getAll(UserDTO filter, Pageable pageable) {
        return userRepository.findAll(filter, pageable);
    }

    /**
     * Lấy user theo ID (throw nếu không tồn tại)
     */
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }

    /**
     * Tạo user mới
     */
    public Long createUser(String username, String email, String passwordHash) {
        return userRepository.create(username, email, passwordHash);
    }

    /**
     * Cập nhật thông tin user
     */
    public int updateUser(Long id, String email, String passwordHash, short status) {
        return userRepository.update(id, email, passwordHash, status);
    }

    /**
     * Xóa user và các quan hệ liên quan
     */
    public int deleteUser(Long userId) {
        return userRepository.delete(userId);
    }

    /**
     * Vô hiệu hóa user (active = 0)
     */
    public int deactivateUser(Long userId) {
        return userRepository.deactivateUser(userId);
    }

    /**
     * Kích hoạt lại user (active = 1)
     */
    public int activateUser(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userRepository.update(userId, user.getEmail(), user.getPasswordHash(), UserStatus.ACTIVE);
    }

    /**
     * Khóa user (active = 3)
     */
    public int lockUser(Long userId, LocalDateTime until) {
        return userRepository.lockUser(userId, until);
    }

    /**
     * Mở khóa user
     */
    public int unlockUser(Long userId) {
        return userRepository.unlockUser(userId);
    }

    /**
     * Lấy toàn bộ user (không phân trang)
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Đặt lại failedAttempts khi đăng nhập thành công
     */
    public void resetFailedAttempts(Long userId) {
        userRepository.resetFailedAttempts(userId);
    }

    /**
     * Tăng failedAttempts khi đăng nhập sai
     */
    public void increaseFailedAttempts(Long userId) {
        userRepository.increaseFailedAttempts(userId);
    }

    /**
     * Cập nhật thời gian đăng nhập cuối
     */
    public void updateLastLogin(Long userId) {
        userRepository.updateLastLogin(userId);
    }
}
