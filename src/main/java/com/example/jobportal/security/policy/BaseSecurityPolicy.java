package com.example.jobportal.security.policy;

import com.example.jobportal.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseSecurityPolicy {

    /**
     * Lấy user hiện tại trong context
     */
    protected CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    /**
     * Kiểm tra user hiện tại có đang active không
     */
    protected boolean isUserActive() {
        var user = getCurrentUser();
        return user != null && user.isActive();
    }

    /**
     * Kiểm tra user hiện tại có role cụ thể hay không
     */
    protected boolean hasRole(String role) {
        var user = getCurrentUser();
        return isUserActive() && user.hasRole(role);
    }

    /**
     * Kiểm tra user hiện tại có quyền ADMIN không
     */
    public boolean isAdmin() {
        return isUserActive() && hasRole("ADMIN");
    }

    /**
     * Lấy ID người dùng hiện tại
     */
    protected Long getCurrentUserId() {
        var user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Kiểm tra userId có phải người hiện tại không
     */
    protected boolean isSelf(Long userId) {
        Long currentId = getCurrentUserId();
        return isUserActive() && currentId != null && currentId.equals(userId);
    }
}
