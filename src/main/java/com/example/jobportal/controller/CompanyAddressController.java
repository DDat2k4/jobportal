package com.example.jobportal.controller;

import com.example.jobportal.data.entity.CompanyAddress;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.CompanyAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/company-addresses")
@RequiredArgsConstructor
public class CompanyAddressController {

    private final CompanyAddressService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_ADDRESS_READ')")
    public ApiResponse<CompanyAddress> getById(@PathVariable Long id) {
        Optional<CompanyAddress> address = service.getById(id);
        return address.map(a -> ApiResponse.ok("CompanyAddress fetched successfully", a))
                .orElseGet(() -> ApiResponse.error("CompanyAddress not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_ADDRESS_READ')")
    public ApiResponse<Page<CompanyAddress>> getAll(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        CompanyAddress filter = new CompanyAddress()
                .setCompanyId(companyId)
                .setCity(city)
                .setCountry(country);

        Page<CompanyAddress> result = service.getAll(filter, pageable);
        return ApiResponse.ok("Company addresses fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPANY_ADDRESS_CREATE')")
    public ApiResponse<Long> create(@RequestBody CompanyAddress address) {
        Long id = service.create(address);
        return ApiResponse.ok("CompanyAddress created successfully", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_ADDRESS_UPDATE')")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody CompanyAddress address) {
        int updated = service.update(id, address);
        return ApiResponse.ok("CompanyAddress updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_ADDRESS_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("CompanyAddress deleted successfully", deleted);
    }
}
