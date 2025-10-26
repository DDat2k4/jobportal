package com.example.jobportal.security.annotations;

import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("jobSecurity")
@RequiredArgsConstructor
public class JobSecurity {

    private final JobRepository jobRepository;

    public boolean isOwner(Long jobId) {
        if (jobId == null) return false;

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        var principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) return false;

        // Admin luôn được phép
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Kiểm tra job có thuộc công ty của user không
        return jobRepository.findById(jobId)
                .map(job -> job.getCompanyId() != null &&
                        userDetails.getCompanyIds().contains(job.getCompanyId()))
                .orElse(false);
    }
}

