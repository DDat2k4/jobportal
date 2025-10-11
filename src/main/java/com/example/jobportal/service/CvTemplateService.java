package com.example.jobportal.service;

import com.example.jobportal.data.entity.CvTemplate;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.CvTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CvTemplateService {

    private final CvTemplateRepository repo;

    public CvTemplateService(CvTemplateRepository repo) {
        this.repo = repo;
    }

    public Optional<CvTemplate> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<CvTemplate> getByCode(String code) {
        return repo.findByCode(code);
    }

    public CvTemplate create(CvTemplate template) {
        return repo.create(template);
    }

    public Optional<CvTemplate> update(Long id, CvTemplate template) {
        return repo.update(id, template);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<CvTemplate> getAll(CvTemplate filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}