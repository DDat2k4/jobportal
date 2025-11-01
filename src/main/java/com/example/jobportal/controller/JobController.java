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
        return jobService.getById(id)
                .map(j -> ApiResponse.ok("Job fetched successfully", j))
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
        if (sortBy != null) {
            pageable.addOrder(sortBy, Boolean.TRUE.equals(asc) ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        Job filter = new Job()
                .setTitle(title)
                .setCompanyId(companyId)
                .setCategoryId(categoryId)
                .setLocation(location);

        return ApiResponse.ok("Jobs fetched successfully", jobService.getAll(filter, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('JOB_CREATE') and @jobSecurity.canAccessCompany(#job.companyId)")
    public ApiResponse<Job> create(@RequestBody Job job) {
        Job created = jobService.create(job);
        return ApiResponse.ok("Job created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_UPDATE') and @jobSecurity.isOwner(#id)")
    public ApiResponse<Job> update(@PathVariable Long id, @RequestBody Job job) {
        Optional<Job> existing = jobService.getById(id);
        if (existing.isEmpty()) {
            return ApiResponse.error("Job not found");
        }

        job.setId(id);
        Job updated = jobService.update(job)
                .orElseThrow(() -> new RuntimeException("Job update failed"));
        return ApiResponse.ok("Job updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_DELETE') and @jobSecurity.isOwner(#id)")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        Optional<Job> existing = jobService.getById(id);
        if (existing.isEmpty()) {
            return ApiResponse.error("Job not found");
        }

        int deleted = jobService.delete(id);
        return ApiResponse.ok("Job deleted successfully", deleted);
    }

    @GetMapping("/count-by-category")
    @PreAuthorize("hasAuthority('JOB_READ')")
    public ApiResponse<?> countJobsByCategory(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Short status
    ) {
        Job filter = new Job()
                .setCompanyId(companyId)
                .setStatus(status);

        return ApiResponse.ok("Job count by category fetched successfully",
                jobService.countJobsByCategory(filter));
    }
}
