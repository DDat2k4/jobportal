package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Application;
import com.example.jobportal.data.pojo.StatusUpdateRequest;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    /**
     * Lấy chi tiết 1 application
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_READ') and @applicationSecurity.canViewOrModify(#id)")
    public ApiResponse<Application> getById(@PathVariable Long id) {
        Optional<Application> application = service.getById(id);
        return application.map(a -> ApiResponse.ok("Application fetched successfully", a))
                .orElseGet(() -> ApiResponse.error("Application not found"));
    }

    /**
     * Lấy danh sách application (phân trang + filter)
     * ADMIN, EMPLOYER hoặc SEEKER có quyền đọc
     */
    @GetMapping
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public ApiResponse<Page<Application>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String status,
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

        Application filter = new Application()
                .setSeekerId(userId)
                .setJobId(jobId)
                .setStatus(status);

        Page<Application> result = service.getAll(filter, pageable);
        return ApiResponse.ok("Applications fetched successfully", result);
    }

    /**
     * Người dùng apply job
     */
    @PostMapping
    @PreAuthorize("hasAuthority('APPLICATION_CREATE')")
    public ApiResponse<Application> create(@RequestBody Application application) {
        Application created = service.create(application);
        return ApiResponse.ok("Application created successfully", created);
    }

    /**
     * Cập nhật application (chỉ chủ sở hữu hoặc employer liên quan)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_UPDATE') and @applicationSecurity.canViewOrModify(#id)")
    public ApiResponse<Application> update(@PathVariable Long id, @RequestBody Application application) {
        Application updated = service.update(id, application)
                .orElseThrow(() -> new RuntimeException("Application not found or update failed"));
        return ApiResponse.ok("Application updated successfully", updated);
    }

    /**
     * Xóa application (chỉ admin hoặc chủ sở hữu)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_DELETE') and @applicationSecurity.canViewOrModify(#id)")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("Application deleted successfully", deleted);
    }

    /**
     * Cập nhật trạng thái (EMPLOYER hoặc ADMIN mới được phép)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('APPLICATION_UPDATE_STATUS') and @applicationSecurity.canViewOrModify(#id)")
    public ApiResponse<Application> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    ) {
        Application updated = service.changeStatus(id, request.getNewStatus(), request.getFeedback());
        return ApiResponse.ok(
                String.format("Application status updated to %s successfully", request.getNewStatus()),
                updated
        );
    }
}
