package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SKILL_READ')")
    public ApiResponse<Skill> getById(@PathVariable Long id) {
        return skillService.get(Skill.builder().id(id).build())
                .map(s -> ApiResponse.ok("Skill fetched successfully", s))
                .orElseGet(() -> ApiResponse.error("Skill not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SKILL_READ')")
    public ApiResponse<Page<Skill>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = new Pageable(page, size);
        Skill filter = new Skill().setName(name);
        return ApiResponse.ok("Skills fetched successfully", skillService.getAll(filter, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SKILL_CREATE')")
    public ApiResponse<Skill> create(@RequestBody Skill skill) {
        Skill created = skillService.create(skill);
        return ApiResponse.ok("Skill created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SKILL_UPDATE')")
    public ApiResponse<Skill> update(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        Optional<Skill> updated = skillService.update(skill);
        return updated
                .map(s -> ApiResponse.ok("Skill updated successfully", s))
                .orElseGet(() -> ApiResponse.error("Skill update failed"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SKILL_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = skillService.delete(new Skill().setId(id));
        return ApiResponse.ok("Skill deleted successfully", deleted);
    }
}
