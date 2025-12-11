package com.example.jobportal.service;

import com.example.jobportal.data.entity.JobSkill;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.JobSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobSkillService {

    private final JobSkillRepository repo;

    public JobSkillService(JobSkillRepository repo) {
        this.repo = repo;
    }

    public Optional<JobSkill> getById(Long id) {
        return repo.findById(id);
    }

    public JobSkill create(JobSkill jobSkill) {
        return repo.create(jobSkill);
    }

    public Optional<JobSkill> update(Long id, JobSkill jobSkill) {
        return repo.update(id, jobSkill);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<JobSkill> getAll(JobSkill filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    /**
     * Lấy tất cả skill của một job
     */
    public List<JobSkill> getByJobId(Long jobId) {
        return repo.findByJobId(jobId);
    }

    public List<String> getSkillNamesByJobId(Long jobId) {
        return repo.findSkillNamesByJobId(jobId);
    }
}
