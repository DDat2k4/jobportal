package com.example.jobportal.controller.learningpath;

import com.example.jobportal.data.entity.learningpath.SkillMapping;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.learningpath.SkillMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-mappings")
@RequiredArgsConstructor
public class SkillMappingController {

    private final SkillMappingService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SKILL_MAPPING_READ')")
    public ApiResponse<SkillMapping> get(@PathVariable Long id) {
        return service.get(id)
                .map(m -> ApiResponse.ok("Mapping fetched successfully", m))
                .orElseGet(() -> ApiResponse.error("Mapping not found"));
    }

    @GetMapping("/by-alias")
    @PreAuthorize("hasAuthority('SKILL_MAPPING_READ')")
    public ApiResponse<SkillMapping> getByAlias(@RequestParam String alias) {
        return service.getByAlias(alias)
                .map(m -> ApiResponse.ok("Mapping fetched successfully", m))
                .orElseGet(() -> ApiResponse.error("Mapping not found"));
    }

    @GetMapping("/by-skill/{skillId}")
    @PreAuthorize("hasAuthority('SKILL_MAPPING_READ')")
    public ApiResponse<List<SkillMapping>> getBySkill(@PathVariable Long skillId) {
        return ApiResponse.ok("Mappings fetched successfully", service.getBySkillId(skillId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SKILL_MAPPING_READ')")
    public ApiResponse<Page<SkillMapping>> getAll(
            @RequestParam(required = false) Long skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = new Pageable(page, size);
        SkillMapping filter = new SkillMapping().setSkillId(skillId);
        return ApiResponse.ok("Mappings fetched successfully", service.getAll(filter, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SKILL_MAPPING_CREATE')")
    public ApiResponse<SkillMapping> create(@RequestBody SkillMapping m) {
        return ApiResponse.ok("Mapping created successfully", service.create(m));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SKILL_MAPPING_UPDATE')")
    public ApiResponse<SkillMapping> update(@PathVariable Long id, @RequestBody SkillMapping m) {
        return service.update(id, m)
                .map(res -> ApiResponse.ok("Mapping updated successfully", res))
                .orElseGet(() -> ApiResponse.error("Update failed"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SKILL_MAPPING_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        return ApiResponse.ok("Mapping deleted successfully", service.delete(id));
    }
}