package com.example.jobportal.service;

import com.example.jobportal.data.entity.CompanySocial;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.CompanySocialRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanySocialService {

    private final CompanySocialRepository repo;

    public CompanySocialService(CompanySocialRepository repo) {
        this.repo = repo;
    }

    public Optional<CompanySocial> getById(Long id) {
        return repo.findById(id);
    }

    public CompanySocial create(CompanySocial companySocial) {
        return repo.create(companySocial);
    }

    public Optional<CompanySocial> update(Long id, CompanySocial companySocial) {
        return repo.update(id, companySocial);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<CompanySocial> getAll(CompanySocial filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}
