package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Application;
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public ApiResponse<Application> getById(@PathVariable Long id) {
        Optional<Application> application = service.getById(id);
        return application.map(a -> ApiResponse.ok("Application fetched successfully", a))
                .orElseGet(() -> ApiResponse.error("Application not found"));
    }

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

        // Nếu có sortBy thì thêm order, không thì dùng mặc định
        if (sortBy != null && !sortBy.isBlank()) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        // Tạo filter entity
        Application filter = new Application()
                .setSeekerId(userId)
                .setJobId(jobId)
                .setStatus(status);

        Page<Application> result = service.getAll(filter, pageable);
        return ApiResponse.ok("Applications fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('APPLICATION_CREATE')")
    public ApiResponse<Long> create(@RequestBody Application application) {
        Long id = service.create(application);
        return ApiResponse.ok("Application created successfully", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_UPDATE')")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody Application application) {
        int updated = service.update(id, application);
        return ApiResponse.ok("Application updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("Application deleted successfully", deleted);
    }
}
