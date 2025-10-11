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

        // setup sort
        if (sortBy != null && !sortBy.isBlank()) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
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
    @PreAuthorize("hasAuthority('USER_CV_CREATE')")
    public ApiResponse<UserCv> create(@RequestBody UserCv cv) {
        UserCv created = service.create(cv);
        return ApiResponse.ok("User CV created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_CV_UPDATE')")
    public ApiResponse<UserCv> update(@PathVariable Long id, @RequestBody UserCv cv) {
        UserCv updated = service.update(id, cv)
                .orElseThrow(() -> new RuntimeException("User CV not found or update failed"));
        return ApiResponse.ok("User CV updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_CV_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("User CV deleted successfully", deleted);
    }

    /**
     * Lấy CV mặc định của user
     */
    @GetMapping("/default/{userId}")
    @PreAuthorize("hasAuthority('USER_CV_READ')")
    public ApiResponse<UserCv> getDefaultByUserId(@PathVariable Long userId) {
        Optional<UserCv> cv = service.getDefaultByUserId(userId);
        return cv.map(c -> ApiResponse.ok("Default CV fetched successfully", c))
                .orElseGet(() -> ApiResponse.error("Default CV not found"));
    }

    /**
     * Lấy CV đầy đủ (bao gồm danh sách section)
     */
    @GetMapping("/{id}/full")
    @PreAuthorize("hasAuthority('USER_CV_READ')")
    public ApiResponse<?> getFullCv(@PathVariable Long id) {
        return service.getFullCv(id)
                .map(full -> ApiResponse.ok("Full CV fetched successfully", full))
                .orElseGet(() -> ApiResponse.error("CV not found"));
    }

    /**
     * Tạo 1 bản CV mới dựa trên id gốc, copy toàn bộ section sang
     */
    @PostMapping("/{id}/clone")
    @PreAuthorize("hasAuthority('USER_CV_CREATE')")
    public ApiResponse<UserCv> cloneCv(@PathVariable Long id) {
        return service.cloneCv(id)
                .map(clone -> ApiResponse.ok("CV cloned successfully", clone))
                .orElseGet(() -> ApiResponse.error("CV not found or clone failed"));
    }

    /**
     * Cập nhật CV + section đồng thời
     */
    @PutMapping("/{id}/full")
    @PreAuthorize("hasAuthority('USER_CV_UPDATE')")
    public ApiResponse<?> updateFullCv(@PathVariable Long id, @RequestBody FullCvResponse payload) {
        boolean updated = service.updateFullCv(id, payload);
        return updated
                ? ApiResponse.ok("Full CV updated successfully", payload)
                : ApiResponse.error("Update failed or CV not found");
    }

}
