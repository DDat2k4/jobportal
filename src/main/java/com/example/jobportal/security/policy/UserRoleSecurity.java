package com.example.jobportal.security.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userRoleSecurity")
@RequiredArgsConstructor
public class UserRoleSecurity extends BaseSecurityPolicy {

    /**
     * Kiểm tra quyền xem hoặc chỉnh sửa role của một user.
     * - ADMIN có toàn quyền.
     * - Chính người dùng có thể xem role của mình.
     */
    public boolean canViewOrModify(Long userId) {
        if (isAdmin()) return true;
        return isSelf(userId);
    }

    /**
     * Kiểm tra quyền thay đổi (thêm / xóa / cập nhật) role của user khác.
     * - Chỉ ADMIN mới được phép thay đổi role người khác.
     */
    public boolean canManageRoles(Long userId) {
        if (isAdmin()) return true;
        // Nếu là chính mình thì chỉ được xem, không được chỉnh role.
        return false;
    }
}
