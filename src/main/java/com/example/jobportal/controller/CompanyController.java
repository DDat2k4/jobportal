package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Company;
import com.example.jobportal.data.entity.EmployerCompany;
import com.example.jobportal.data.pojo.CompanyFullDTO;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.CompanyInfoService;
import com.example.jobportal.service.CompanyService;
import com.example.jobportal.service.EmployerCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final EmployerCompanyService employerCompanyService;
    private final CompanyInfoService companyInfoService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_READ')")
    public ApiResponse<Company> getById(@PathVariable Long id) {
        Optional<Company> company = companyService.getById(id);
        return company.map(c -> ApiResponse.ok("Company fetched successfully", c))
                .orElseGet(() -> ApiResponse.error("Company not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_READ')")
    public ApiResponse<Page<Company>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String website,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        // Sort
        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        // Filter
        Company filter = new Company()
                .setName(name)
                .setWebsite(website);

        Page<Company> result = companyService.getAll(filter, pageable);
        return ApiResponse.ok("Companies fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPANY_CREATE')")
    public ApiResponse<Company> create(@RequestBody Company company,
                                       @AuthenticationPrincipal(expression = "id") Long employerId) {
        // Tạo công ty
        Company created = companyService.create(company);

        //Tạo liên kết Employer ↔ Company
        employerCompanyService.create(
                new EmployerCompany()
                        .setEmployerId(employerId)
                        .setCompanyId(created.getId())
        );

        return ApiResponse.ok("Company created successfully", created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_UPDATE') and @companySecurity.canAccessCompany(#id)")
    public ApiResponse<Company> update(@PathVariable Long id, @RequestBody Company company) {
        company.setId(id);
        Company updated = companyService.update(id, company)
                .orElseThrow(() -> new RuntimeException("Company not found or update failed"));
        return ApiResponse.ok("Company updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_DELETE') and @companySecurity.canAccessCompany(#id)")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = companyService.delete(id);
        return ApiResponse.ok("Company deleted successfully", deleted);
    }

    @GetMapping("/{id}/full")
    @PreAuthorize("hasAuthority('COMPANY_READ')")
    public ApiResponse<CompanyFullDTO> getFullCompanyInfo(@PathVariable Long id) {
        return companyInfoService.getFullCompanyInfo(id)
                .map(dto -> ApiResponse.ok("Full company info fetched successfully", dto))
                .orElseGet(() -> ApiResponse.error("Company not found"));
    }
}
