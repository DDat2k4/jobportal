package com.example.jobportal.controller;

import com.example.jobportal.data.entity.EmployerCompany;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.EmployerCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/employer-companies")
@RequiredArgsConstructor
public class EmployerCompanyController {

    private final EmployerCompanyService service;

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_READ')")
    public ApiResponse<EmployerCompany> get(@RequestParam(required = false) Long employerId,
                                            @RequestParam(required = false) Long companyId) {
        EmployerCompany filter = new EmployerCompany().setEmployerId(employerId).setCompanyId(companyId);
        Optional<EmployerCompany> ec = service.get(filter);
        return ec.map(e -> ApiResponse.ok("EmployerCompany fetched successfully", e))
                .orElseGet(() -> ApiResponse.error("EmployerCompany not found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_CREATE')")
    public ApiResponse<Integer> create(@RequestBody EmployerCompany ec) {
        int created = service.create(ec);
        return ApiResponse.ok("EmployerCompany created successfully", created);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_DELETE')")
    public ApiResponse<Integer> delete(@RequestBody EmployerCompany ec) {
        int deleted = service.delete(ec);
        return ApiResponse.ok("EmployerCompany deleted successfully", deleted);
    }
}
