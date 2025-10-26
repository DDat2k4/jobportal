package com.example.jobportal.security.annotations;

import com.example.jobportal.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("companySecurity")
public class CompanySecurity {

    public boolean canAccessCompany(Long companyId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        var principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            // ADMIN bỏ qua
            if (userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return true;
            }

            // Kiểm tra companyId
            return userDetails.getCompanyIds().contains(companyId);
        }

        return false;
    }
}
