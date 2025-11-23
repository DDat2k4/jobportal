package com.example.jobportal.security.policy;

import com.example.jobportal.data.entity.UserProfile;
import com.example.jobportal.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userProfileSecurity")
@RequiredArgsConstructor
public class UserProfileSecurity extends BaseSecurityPolicy {

    private final UserProfileRepository userProfileRepository;

    /**
     * Kiểm tra xem user hiện tại có quyền xem hoặc chỉnh sửa UserProfile với id chỉ định không.
     * - ADMIN có toàn quyền.
     * - Người sở hữu (cùng userId) có quyền xem/chỉnh sửa hồ sơ của chính mình.
     */
    public boolean canViewOrModify(Long profileId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;

        // Kiểm tra xem profile này có thuộc về user hiện tại không
        return userProfileRepository.findById(profileId)
                .map(profile -> isSelf(profile.getUserId()))
                .orElse(false);
    }

    /**
     * Kiểm tra xem user hiện tại có quyền xem/chỉnh sửa hồ sơ theo userId hay không.
     * - ADMIN có toàn quyền.
     * - Người dùng chỉ được phép thao tác với hồ sơ của chính họ.
     */
    public boolean canAccessByUserId(Long userId) {
        if (isAdmin()) return true;
        return isSelf(userId);
    }

    public boolean canCreate(UserProfile profile) {
        var user = getCurrentUser();
        if (user == null) return false;

        if (isAdmin()) return true;

        // User thường chỉ tạo profile cho chính mình
        return profile.getUserId() != null && isSelf(profile.getUserId());
    }

}
