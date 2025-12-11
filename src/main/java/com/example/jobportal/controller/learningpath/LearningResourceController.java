package com.example.jobportal.controller.learningpath;

import com.example.jobportal.data.entity.learningpath.LearningResource;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.learningpath.LearningResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-resources")
@RequiredArgsConstructor
public class LearningResourceController {

    private final LearningResourceService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEARNING_RESOURCE_READ')")
    public ApiResponse<LearningResource> get(@PathVariable Long id) {
        return service.get(id)
                .map(r -> ApiResponse.ok("Resource fetched successfully", r))
                .orElseGet(() -> ApiResponse.error("Resource not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEARNING_RESOURCE_READ')")
    public ApiResponse<Page<LearningResource>> getAll(
            @RequestParam(required = false) Long skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = new Pageable(page, size);
        LearningResource filter = new LearningResource().setSkillId(skillId);
        return ApiResponse.ok("Resources fetched successfully", service.getAll(filter, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEARNING_RESOURCE_CREATE')")
    public ApiResponse<LearningResource> create(@RequestBody LearningResource r) {
        return ApiResponse.ok("Resource created successfully", service.create(r));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEARNING_RESOURCE_UPDATE')")
    public ApiResponse<LearningResource> update(@PathVariable Long id, @RequestBody LearningResource r) {
        return service.update(id, r)
                .map(res -> ApiResponse.ok("Resource updated successfully", res))
                .orElseGet(() -> ApiResponse.error("Update failed"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEARNING_RESOURCE_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        return ApiResponse.ok("Resource deleted successfully", service.delete(id));
    }
}