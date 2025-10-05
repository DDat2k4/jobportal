package com.example.jobportal.service;

import com.example.jobportal.data.entity.EmployerCompany;
import com.example.jobportal.repository.EmployerCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployerCompanyService {

    private final EmployerCompanyRepository repo;

    public EmployerCompanyService(EmployerCompanyRepository repo) {
        this.repo = repo;
    }

    public Optional<EmployerCompany> get(EmployerCompany employerCompany) {
        return repo.find(employerCompany);
    }

    public int create(EmployerCompany employerCompany) {
        return repo.create(employerCompany);
    }

    public int delete(EmployerCompany employerCompany) {
        return repo.delete(employerCompany);
    }
}
