package com.example.jobportal.controller;

import com.example.jobportal.data.entity.JobSkill;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.JobSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/job-skills")
@RequiredArgsConstructor
public class JobSkillController {

    private final JobSkillService jobSkillService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_SKILL_READ')")
    public ApiResponse<JobSkill> getById(@PathVariable Long id) {
        return jobSkillService.getById(id)
                .map(js -> ApiResponse.ok("JobSkill fetched successfully", js))
                .orElseGet(() -> ApiResponse.error("JobSkill not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('JOB_SKILL_READ')")
    public ApiResponse<Page<JobSkill>> getAll(
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) Long skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = new Pageable(page, size);
        JobSkill filter = new JobSkill().setJobId(jobId).setSkillId(skillId);
        return ApiResponse.ok("JobSkills fetched successfully", jobSkillService.getAll(filter, pageable));
    }

    @GetMapping("/by-job/{jobId}")
    @PreAuthorize("hasAuthority('JOB_SKILL_READ')")
    public ApiResponse<List<JobSkill>> getByJobId(@PathVariable Long jobId) {
        List<JobSkill> skills = jobSkillService.getByJobId(jobId);
        return ApiResponse.ok("JobSkills for job fetched successfully", skills);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('JOB_SKILL_CREATE')")
    public ApiResponse<JobSkill> create(@RequestBody JobSkill jobSkill) {
        JobSkill created = jobSkillService.create(jobSkill);
        return ApiResponse.ok("JobSkill created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_SKILL_UPDATE')")
    public ApiResponse<JobSkill> update(@PathVariable Long id, @RequestBody JobSkill jobSkill) {
        Optional<JobSkill> updated = jobSkillService.update(id, jobSkill);
        return updated
                .map(js -> ApiResponse.ok("JobSkill updated successfully", js))
                .orElseGet(() -> ApiResponse.error("JobSkill update failed"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_SKILL_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = jobSkillService.delete(id);
        return ApiResponse.ok("JobSkill deleted successfully", deleted);
    }
}
