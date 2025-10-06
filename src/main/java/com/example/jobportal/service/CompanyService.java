package com.example.jobportal.service;

import com.example.jobportal.data.entity.Company;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository repo;

    public CompanyService(CompanyRepository repo) {
        this.repo = repo;
    }

    public Optional<Company> getById(Long id) {
        return repo.findById(id);
    }

    public Company create(Company company) {
        return repo.create(company);
    }

    public Optional<Company> update(Long id, Company company) {
        return repo.update(id, company);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Company> getAll(Company filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}
