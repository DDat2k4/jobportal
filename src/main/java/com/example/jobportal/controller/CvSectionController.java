package com.example.jobportal.controller;

import com.example.jobportal.data.entity.CvSection;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.CvSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cv-sections")
@RequiredArgsConstructor
public class CvSectionController {

    private final CvSectionService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CV_SECTION_READ') and @userSecurity.canAccessCvSection(#id)")
    public ApiResponse<CvSection> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(section -> ApiResponse.ok("Fetched successfully", section))
                .orElseGet(() -> ApiResponse.error("CV Section not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CV_SECTION_READ')")
    public ApiResponse<Page<CvSection>> getAll(
            @RequestParam(required = false) Long cvId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        if (sortBy != null && !sortBy.isBlank()) {
            pageable.addOrder(sortBy, Boolean.TRUE.equals(asc) ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("position");
        }

        CvSection filter = new CvSection()
                .setCvId(cvId)
                .setType(type)
                .setTitle(title);

        Page<CvSection> result = service.getAll(filter, pageable);
        return ApiResponse.ok("CV Sections fetched successfully", result);
    }

    @GetMapping("/by-cv/{cvId}")
    @PreAuthorize("hasAuthority('CV_SECTION_READ') and @userSecurity.canAccessCv(#cvId)")
    public ApiResponse<List<CvSection>> getByCvId(@PathVariable Long cvId) {
        List<CvSection> sections = service.getByCvId(cvId);
        return ApiResponse.ok("Fetched sections for CV ID: " + cvId, sections);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CV_SECTION_CREATE') and @userSecurity.canAccessCv(#section.cvId)")
    public ApiResponse<CvSection> create(@RequestBody CvSection section) {
        CvSection created = service.create(section);
        return ApiResponse.ok("CV Section created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CV_SECTION_UPDATE') and @userSecurity.canAccessCvSection(#id)")
    public ApiResponse<CvSection> update(@PathVariable Long id, @RequestBody CvSection section) {
        Optional<CvSection> updated = service.update(id, section);
        return updated.map(s -> ApiResponse.ok("CV Section updated successfully", s))
                .orElseGet(() -> ApiResponse.error("CV Section not found or update failed"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CV_SECTION_DELETE') and @userSecurity.canAccessCvSection(#id)")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return deleted > 0
                ? ApiResponse.ok("CV Section deleted successfully", null)
                : ApiResponse.error("CV Section not found");
    }

    @DeleteMapping("/by-cv/{cvId}")
    @PreAuthorize("hasAuthority('CV_SECTION_DELETE') and @userSecurity.canAccessCv(#cvId)")
    public ApiResponse<Integer> deleteByCvId(@PathVariable Long cvId) {
        int deleted = service.deleteByCvId(cvId);
        return ApiResponse.ok("Deleted all sections for CV ID: " + cvId, deleted);
    }
}
