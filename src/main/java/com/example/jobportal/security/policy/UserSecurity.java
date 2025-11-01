package com.example.jobportal.security.policy;

import com.example.jobportal.service.UserCvService;
import com.example.jobportal.service.CvSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity extends BaseSecurityPolicy {

    private final UserCvService userCvService;
    private final CvSectionService cvSectionService;

    public boolean canAccessUser(Long userId) {
        if (isAdmin()) return true;
        return isSelf(userId);
    }

    public boolean canAccessCv(Long cvId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;

        return userCvService.getById(cvId)
                .map(cv -> cv.getUserId().equals(user.getId()))
                .orElse(false);
    }

    public boolean canAccessCvSection(Long sectionId) {
        var user = getCurrentUser();
        if (user == null) return false;
        if (isAdmin()) return true;

        return cvSectionService.getById(sectionId)
                .flatMap(section -> userCvService.getById(section.getCvId()))
                .map(cv -> cv.getUserId().equals(user.getId()))
                .orElse(false);
    }
}
