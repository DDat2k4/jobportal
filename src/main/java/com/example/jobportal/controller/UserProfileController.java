package com.example.jobportal.controller;

import com.example.jobportal.data.entity.UserProfile;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    /**
     * Lấy chi tiết hồ sơ người dùng theo ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_PROFILE_READ') and @userProfileSecurity.canViewOrModify(#id)")
    public ApiResponse<UserProfile> getById(@PathVariable Long id) {
        Optional<UserProfile> profile = service.getById(id);
        return profile.map(p -> ApiResponse.ok("User profile fetched successfully", p))
                .orElseGet(() -> ApiResponse.error("User profile not found"));
    }

    /**
     * Lấy hồ sơ theo userId (tức tài khoản người dùng)
     */
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAuthority('USER_PROFILE_READ') and @userProfileSecurity.canAccessByUserId(#userId)")
    public ApiResponse<UserProfile> getByUserId(@PathVariable Long userId) {
        Optional<UserProfile> profile = service.getByUserId(userId);
        return profile.map(p -> ApiResponse.ok("User profile fetched successfully", p))
                .orElseGet(() -> ApiResponse.error("User profile not found"));
    }

    /**
     * Lấy danh sách hồ sơ (có phân trang + filter)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_PROFILE_READ')")
    public ApiResponse<Page<UserProfile>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Short gender,
            @RequestParam(required = false) String headline,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        if (sortBy != null && !sortBy.isBlank()) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        UserProfile filter = new UserProfile()
                .setName(name)
                .setGender(gender)
                .setHeadline(headline);

        Page<UserProfile> result = service.getAll(filter, pageable);
        return ApiResponse.ok("User profiles fetched successfully", result);
    }

    /**
     * Tạo mới hồ sơ người dùng
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_PROFILE_CREATE') and @userProfileSecurity.canCreate(#profile)")
    public ApiResponse<UserProfile> create(@RequestBody UserProfile profile) {
        UserProfile created = service.create(profile);
        return ApiResponse.ok("User profile created successfully", created);
    }

    /**
     * Cập nhật hồ sơ người dùng
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_PROFILE_UPDATE') and @userProfileSecurity.canViewOrModify(#id)")
    public ApiResponse<UserProfile> update(@PathVariable Long id, @RequestBody UserProfile profile) {
        profile.setId(id);
        UserProfile updated = service.update(profile)
                .orElseThrow(() -> new RuntimeException("User profile not found or update failed"));
        return ApiResponse.ok("User profile updated successfully", updated);
    }

    /**
     * Xóa hồ sơ người dùng
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_PROFILE_DELETE') and @userProfileSecurity.canAccessByUserId(#userId)")
    public ApiResponse<Integer> deleteByUserId(@PathVariable Long userId) {
        int deleted = service.deleteByUserId(userId);
        return ApiResponse.ok("User profile deleted successfully", deleted);
    }

    /**
     * Cập nhật nhanh tên & avatar cơ bản (chủ sở hữu hoặc admin)
     */
    @PatchMapping("/{userId}/basic")
    @PreAuthorize("hasAuthority('USER_PROFILE_UPDATE') and @userProfileSecurity.canAccessByUserId(#userId)")
    public ApiResponse<Void> updateBasic(
            @PathVariable Long userId,
            @RequestParam String name,
            @RequestParam String avatar
    ) {
        service.updateBasic(userId, name, avatar);
        return ApiResponse.ok("User basic info updated successfully", null);
    }
}
