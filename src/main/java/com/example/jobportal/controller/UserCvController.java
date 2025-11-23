package com.example.jobportal.controller;

import com.example.jobportal.data.entity.UserCv;
import com.example.jobportal.data.pojo.FullCvResponse;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.UserCvService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user-cvs")
@RequiredArgsConstructor
public class UserCvController {

    private final UserCvService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_CV_READ')")
    public ApiResponse<UserCv> getById(@PathVariable Long id) {
        Optional<UserCv> cv = service.getById(id);
        return cv.map(c -> ApiResponse.ok("User CV fetched successfully", c))
                .orElseGet(() -> ApiResponse.error("User CV not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_CV_READ')")
    public ApiResponse<Page<UserCv>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String templateCode,
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        if (sortBy != null && !sortBy.isBlank()) {
            pageable.addOrder(sortBy, Boolean.TRUE.equals(asc) ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        UserCv filter = new UserCv()
                .setUserId(userId)
                .setTitle(title)
                .setTemplateCode(templateCode)
                .setIsDefault(isDefault);

        Page<UserCv> result = service.getAll(filter, pageable);
        return ApiResponse.ok("User CVs fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CV_CREATE') and @userSecurity.canAccessUser(#cv.userId)")
    public ApiResponse<UserCv> create(@RequestBody UserCv cv) {
        UserCv created = service.create(cv);
        return ApiResponse.ok("User CV created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_CV_UPDATE') and @userSecurity.canAccessCv(#id)")
    public ApiResponse<UserCv> update(@PathVariable Long id, @RequestBody UserCv cv) {
        UserCv updated = service.update(id, cv)
                .orElseThrow(() -> new RuntimeException("User CV not found or update failed"));
        return ApiResponse.ok("User CV updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_CV_DELETE') and @userSecurity.canAccessCv(#id)")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("User CV deleted successfully", deleted);
    }

    @GetMapping("/default/{userId}")
    @PreAuthorize("hasAuthority('USER_CV_READ') and @userSecurity.canAccessUser(#userId)")
    public ApiResponse<UserCv> getDefaultByUserId(@PathVariable Long userId) {
        Optional<UserCv> cv = service.getDefaultByUserId(userId);
        return cv.map(c -> ApiResponse.ok("Default CV fetched successfully", c))
                .orElseGet(() -> ApiResponse.error("Default CV not found"));
    }

    @GetMapping("/{id}/full")
    @PreAuthorize("hasAuthority('USER_CV_READ')")
    public ApiResponse<?> getFullCv(@PathVariable Long id) {
        return service.getFullCv(id)
                .map(full -> ApiResponse.ok("Full CV fetched successfully", full))
                .orElseGet(() -> ApiResponse.error("CV not found"));
    }

    @PostMapping("/{id}/clone")
    @PreAuthorize("hasAuthority('USER_CV_CREATE') and @userSecurity.canAccessCv(#id)")
    public ApiResponse<UserCv> cloneCv(@PathVariable Long id) {
        return service.cloneCv(id)
                .map(clone -> ApiResponse.ok("CV cloned successfully", clone))
                .orElseGet(() -> ApiResponse.error("CV not found or clone failed"));
    }

    @PutMapping("/{id}/full")
    @PreAuthorize("hasAuthority('USER_CV_UPDATE') and @userSecurity.canAccessCv(#id)")
    public ApiResponse<?> updateFullCv(@PathVariable Long id, @RequestBody FullCvResponse payload) {
        boolean updated = service.updateFullCv(id, payload);
        return updated
                ? ApiResponse.ok("Full CV updated successfully", payload)
                : ApiResponse.error("Update failed or CV not found");
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasAuthority('USER_CV_UPDATE') and @userSecurity.canAccessCv(#id)")
    public ApiResponse<UserCv> setDefault(@PathVariable Long id) {
        return service.setDefault(id)
                .map(updated -> ApiResponse.ok("CV set as default successfully", updated))
                .orElseGet(() -> ApiResponse.error("CV not found or update failed"));
    }
}
