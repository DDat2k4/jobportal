package com.example.jobportal.service;

import com.example.jobportal.data.entity.Job;
import com.example.jobportal.data.pojo.CategoryJobCount;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository repo;

    public JobService(JobRepository repo) {
        this.repo = repo;
    }

    public Optional<Job> getById(Long id) {
        return repo.findById(id);
    }

    public Job create(Job job) {
        return repo.create(job);
    }

    public Optional<Job> update(Job job) {
        return repo.update(job);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Job> getAll(Job filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    /**
     * Đếm số lượng job theo từng category, có thể lọc theo điều kiện (companyId, status, title, v.v.)
     */
    public List<CategoryJobCount> countJobsByCategory(Job filter) {
        return repo.countJobsByCategory(filter);
    }
}
