package com.example.jobportal.service;

import com.example.jobportal.data.entity.EmployerCompany;
import com.example.jobportal.repository.EmployerCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployerCompanyService {

    private final EmployerCompanyRepository repo;

    public EmployerCompanyService(EmployerCompanyRepository repo) {
        this.repo = repo;
    }

    public List<EmployerCompany> get(EmployerCompany employerCompany) {
        return repo.find(employerCompany);
    }

    public EmployerCompany create(EmployerCompany employerCompany) {
        return repo.create(employerCompany);
    }

    public Optional<EmployerCompany> update(Long employerId, EmployerCompany employerCompany) {
        return repo.update(employerId, employerCompany);
    }

    public int delete(EmployerCompany employerCompany) {
        return repo.delete(employerCompany);
    }

    // Lấy danh sách công ty mà employer đang thuộc về
    public List<EmployerCompany> getCompaniesByEmployerId(Long employerId) {
        return repo.findCompaniesByEmployerId(employerId);
    }
}
