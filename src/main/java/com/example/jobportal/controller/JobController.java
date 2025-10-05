package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Job;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_READ')")
    public ApiResponse<Job> getById(@PathVariable Long id) {
        Optional<Job> job = jobService.getById(id);
        return job.map(j -> ApiResponse.ok("Job fetched successfully", j))
                .orElseGet(() -> ApiResponse.error("Job not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('JOB_READ')")
    public ApiResponse<Page<Job>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        // Sắp xếp động
        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        // Lọc theo các tham số
        Job filter = new Job()
                .setTitle(title)
                .setCompanyId(companyId)
                .setCategoryId(categoryId)
                .setLocation(location);

        Page<Job> result = jobService.getAll(filter, pageable);
        return ApiResponse.ok("Jobs fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('JOB_CREATE')")
    public ApiResponse<Long> create(@RequestBody Job job) {
        Long id = jobService.create(job);
        return ApiResponse.ok("Job created successfully", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody Job job) {
        job.setId(id);
        int updated = jobService.update(job);
        return ApiResponse.ok("Job updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = jobService.delete(id);
        return ApiResponse.ok("Job deleted successfully", deleted);
    }
}
