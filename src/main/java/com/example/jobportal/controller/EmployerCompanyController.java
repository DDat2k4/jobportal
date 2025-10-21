package com.example.jobportal.controller;

import com.example.jobportal.data.entity.EmployerCompany;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.EmployerCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employer-companies")
@RequiredArgsConstructor
public class EmployerCompanyController {

    private final EmployerCompanyService service;

    // Xem mình (employer) đang ở những công ty nào
    @GetMapping("/employers/{employerId}/companies")
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_READ')")
    public ApiResponse<List<EmployerCompany>> getCompaniesByEmployer(@PathVariable Long employerId) {
        List<EmployerCompany> companies = service.getCompaniesByEmployerId(employerId);
        return ApiResponse.ok("Companies fetched successfully for employer", companies);
    }

    // Xem ai đang trong công ty
    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_READ')")
    public ApiResponse<List<EmployerCompany>> get(@RequestParam(required = false) Long employerId,
                                                  @RequestParam(required = false) Long companyId) {
        EmployerCompany filter = new EmployerCompany().setEmployerId(employerId).setCompanyId(companyId);
        List<EmployerCompany> ec = service.get(filter);
        return ApiResponse.ok("EmployerCompany fetched successfully", ec);
    }

    // Thêm người khác vào công ty
    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_CREATE')")
    public ApiResponse<EmployerCompany> create(@RequestBody EmployerCompany ec) {
        EmployerCompany created = service.create(ec);
        return ApiResponse.ok("EmployerCompany created successfully", created);
    }

    @PutMapping("/{employerId}")
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_UPDATE')")
    public ApiResponse<EmployerCompany> update(@PathVariable Long employerId, @RequestBody EmployerCompany ec) {
        EmployerCompany updated = service.update(employerId, ec)
                .orElseThrow(() -> new RuntimeException("EmployerCompany not found or update failed"));
        return ApiResponse.ok("EmployerCompany updated successfully", updated);
    }
    // Xóa người khỏi công ty
    @DeleteMapping
    @PreAuthorize("hasAuthority('EMPLOYER_COMPANY_DELETE')")
    public ApiResponse<Integer> delete(@RequestBody EmployerCompany ec) {
        int deleted = service.delete(ec);
        return ApiResponse.ok("EmployerCompany deleted successfully", deleted);
    }
}
