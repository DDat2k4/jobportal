package com.example.jobportal.service;

import com.example.jobportal.data.entity.CompanyAddress;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.CompanyAddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyAddressService {

    private final CompanyAddressRepository repo;

    public CompanyAddressService(CompanyAddressRepository repo) {
        this.repo = repo;
    }

    public Optional<CompanyAddress> getById(Long id) {
        return repo.findById(id);
    }

    public CompanyAddress create(CompanyAddress companyAddress) {
        return repo.create(companyAddress);
    }

    public Optional<CompanyAddress> update(Long id, CompanyAddress companyAddress) {
        return repo.update(id,
                companyAddress);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<CompanyAddress> getAll(CompanyAddress filter, Pageable pageable) {
        return repo.findAll(filter,
                pageable);
    }
}
