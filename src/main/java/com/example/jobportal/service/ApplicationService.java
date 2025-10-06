package com.example.jobportal.service;

import com.example.jobportal.data.entity.Application;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository repo;

    public ApplicationService(ApplicationRepository repo) {
        this.repo = repo;
    }

    public Optional<Application> getById(Long id) {
        return repo.findById(id);
    }

    public Application create(Application application) {
        return repo.create(application);
    }

    public Optional<Application> update(Long id, Application application) {
        return repo.update(id, application);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Application> getAll(Application filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}

