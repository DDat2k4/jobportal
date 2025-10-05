package com.example.jobportal.controller;

import com.example.jobportal.data.entity.CompanySocial;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.CompanySocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/company-socials")
@RequiredArgsConstructor
public class CompanySocialController {

    private final CompanySocialService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_SOCIAL_READ')")
    public ApiResponse<CompanySocial> getById(@PathVariable Long id) {
        Optional<CompanySocial> cs = service.getById(id);
        return cs.map(c -> ApiResponse.ok("CompanySocial fetched successfully", c))
                .orElseGet(() -> ApiResponse.error("CompanySocial not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_SOCIAL_READ')")
    public ApiResponse<Page<CompanySocial>> getAll(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String platform, // ví dụ: "facebook", "linkedin"
            @RequestParam(required = false) String url,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        // Sắp xếp
        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        // Lọc
        CompanySocial filter = new CompanySocial()
                .setCompanyId(companyId)
                .setPlatform(platform)
                .setUrl(url);

        Page<CompanySocial> result = service.getAll(filter, pageable);
        return ApiResponse.ok("Company socials fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPANY_SOCIAL_CREATE')")
    public ApiResponse<Long> create(@RequestBody CompanySocial cs) {
        Long id = service.create(cs);
        return ApiResponse.ok("CompanySocial created successfully", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_SOCIAL_UPDATE')")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody CompanySocial cs) {
        int updated = service.update(id, cs);
        return ApiResponse.ok("CompanySocial updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_SOCIAL_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("CompanySocial deleted successfully", deleted);
    }
}
