package com.example.jobportal.service;

import com.example.jobportal.data.entity.JobCategory;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.JobCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobCategoryService {

    private final JobCategoryRepository repo;

    public JobCategoryService(JobCategoryRepository repo) {
        this.repo = repo;
    }

    public Optional<JobCategory> get(JobCategory jobCategory) {
        return repo.find(jobCategory);
    }

    public Long create(JobCategory jobCategory) {
        return repo.create(jobCategory);
    }

    public int update(JobCategory jobCategory) {
        return repo.update(jobCategory);
    }

    public int delete(JobCategory jobCategory) {
        return repo.delete(jobCategory);
    }

    public Page<JobCategory> getAll(JobCategory filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}
