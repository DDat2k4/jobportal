package com.example.jobportal.security.policy;

import com.example.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("applicationSecurity")
@RequiredArgsConstructor
public class ApplicationSecurity extends BaseSecurityPolicy {

    private final JobRepository jobRepository;

    public boolean canViewOrModify(Long applicationId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;

        return jobRepository.findApplicationWithJobCompany(applicationId)
                .map(app -> {
                    if (user.hasRole("JOB_SEEKER") && app.getSeekerId().equals(user.getId()))
                        return true;
                    if (user.hasRole("EMPLOYER") &&
                            user.getCompanyIds().contains(app.getCompanyId()))
                        return true;
                    return false;
                })
                .orElse(false);
    }

    public boolean canUpdateStatus(Long applicationId, String newStatus) {
        var user = getCurrentUser();
        if (user == null) return false;

        // Admin full quyền
        if (isAdmin()) return true;

        return jobRepository.findApplicationWithJobCompany(applicationId)
                .map(app -> {
                    // Nếu là SEEKER: chỉ được đổi sang "CANCELED"
                    if (user.hasRole("JOB_SEEKER")) {
                        return app.getSeekerId().equals(user.getId()) &&
                                "CANCELED".equalsIgnoreCase(newStatus);
                    }
                    // Nếu là EMPLOYER: được đổi status theo TRANSITIONS
                    if (user.hasRole("EMPLOYER")) {
                        return user.getCompanyIds().contains(app.getCompanyId());
                    }
                    return false;
                })
                .orElse(false);
    }

}
