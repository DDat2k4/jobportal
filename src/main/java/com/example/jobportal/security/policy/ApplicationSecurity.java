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
                    if (user.hasRole("SEEKER") && app.getSeekerId().equals(user.getId()))
                        return true;
                    if (user.hasRole("EMPLOYER") &&
                            user.getCompanyIds().contains(app.getCompanyId()))
                        return true;
                    return false;
                })
                .orElse(false);
    }
}
