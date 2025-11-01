package com.example.jobportal.security.policy;

import org.springframework.stereotype.Component;

@Component("companySecurity")
public class CompanySecurity extends BaseSecurityPolicy {

    public boolean canAccessCompany(Long companyId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;
        return user.getCompanyIds().contains(companyId);
    }
}
