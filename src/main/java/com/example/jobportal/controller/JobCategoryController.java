package com.example.jobportal.controller;

import com.example.jobportal.data.entity.JobCategory;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/job-categories")
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService service;

    // Lấy theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_CATEGORY_READ')")
    public ApiResponse<JobCategory> getById(@PathVariable Long id) {
        Optional<JobCategory> category = service.get(new JobCategory().setId(id));
        return category.map(c -> ApiResponse.ok("Job category fetched successfully", c))
                .orElseGet(() -> ApiResponse.error("Job category not found"));
    }

    // Lấy danh sách phân trang + filter + sort
    @GetMapping
    @PreAuthorize("hasAuthority('JOB_CATEGORY_READ')")
    public ApiResponse<Page<JobCategory>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);
        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }
        Page<JobCategory> result = service.getAll(new JobCategory().setName(name), pageable);
        return ApiResponse.ok("Job categories fetched successfully", result);
    }

    // Tạo mới
    @PostMapping
    @PreAuthorize("hasAuthority('JOB_CATEGORY_CREATE')")
    public ApiResponse<Long> create(@RequestBody JobCategory category) {
        Long id = service.create(category);
        return ApiResponse.ok("Job category created successfully", id);
    }

    // Cập nhật
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_CATEGORY_UPDATE')")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody JobCategory category) {
        category.setId(id);
        int updated = service.update(category);
        return ApiResponse.ok("Job category updated successfully", updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_CATEGORY_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(new JobCategory().setId(id));
        return ApiResponse.ok("Job category deleted successfully", deleted);
    }
}
