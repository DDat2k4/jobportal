package com.example.jobportal.security.policy;

import com.example.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("jobSecurity")
@RequiredArgsConstructor
public class JobSecurity extends BaseSecurityPolicy {

    private final JobRepository jobRepository;

    public boolean canAccessCompany(Long companyId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;
        return user.getCompanyIds().contains(companyId);
    }

    public boolean isOwner(Long jobId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;

        return jobRepository.findById(jobId)
                .map(job -> job.getCompanyId() != null &&
                        user.getCompanyIds().contains(job.getCompanyId()))
                .orElse(false);
    }
}
