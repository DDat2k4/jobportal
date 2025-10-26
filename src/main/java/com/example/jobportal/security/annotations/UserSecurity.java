package com.example.jobportal.security.annotations;

import com.example.jobportal.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean canAccessUser(Long userId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        var principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            // Admin được phép thao tác với tất cả user
            if (userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return true;
            }
            // User chỉ thao tác với chính mình
            return userDetails.getId().equals(userId);
        }

        return false;
    }
}

