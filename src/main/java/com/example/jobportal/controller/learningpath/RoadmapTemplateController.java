package com.example.jobportal.controller.learningpath;

import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.learningpath.RoadmapTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmap-templates")
@RequiredArgsConstructor
public class RoadmapTemplateController {

    private final RoadmapTemplateService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROADMAP_TEMPLATE_READ')")
    public ApiResponse<RoadmapTemplate> get(@PathVariable Long id) {
        return service.get(id)
                .map(r -> ApiResponse.ok("Template fetched successfully", r))
                .orElseGet(() -> ApiResponse.error("Template not found"));
    }

    @GetMapping("/by-skill")
    @PreAuthorize("hasAuthority('ROADMAP_TEMPLATE_READ')")
    public ApiResponse<List<RoadmapTemplate>> getBySkillName(
            @RequestParam String skillName
    ) {
        return ApiResponse.ok("Templates fetched", service.getBySkillName(skillName)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROADMAP_TEMPLATE_READ')")
    public ApiResponse<Page<RoadmapTemplate>> getAll(
            @RequestParam(required = false) Long skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = new Pageable(page, size);
        RoadmapTemplate filter = new RoadmapTemplate().setSkillId(skillId);
        return ApiResponse.ok("Templates fetched successfully", service.getAll(filter, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROADMAP_TEMPLATE_CREATE')")
    public ApiResponse<RoadmapTemplate> create(@RequestBody RoadmapTemplate t) {
        return ApiResponse.ok("Template created successfully", service.create(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROADMAP_TEMPLATE_UPDATE')")
    public ApiResponse<RoadmapTemplate> update(@PathVariable Long id, @RequestBody RoadmapTemplate t) {
        return service.update(id, t)
                .map(res -> ApiResponse.ok("Template updated successfully", res))
                .orElseGet(() -> ApiResponse.error("Update failed"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROADMAP_TEMPLATE_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        return ApiResponse.ok("Template deleted successfully", service.delete(id));
    }
}