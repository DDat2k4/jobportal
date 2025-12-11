package com.example.jobportal.service;

import com.example.jobportal.data.entity.Job;
import com.example.jobportal.data.entity.JobSkill;
import com.example.jobportal.data.pojo.CategoryJobCount;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.JobSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository repo;
    private final JobSkillRepository jobSkillRepo;

    public JobService(JobRepository repo, JobSkillRepository jobSkillRepo) {
        this.repo = repo;
        this.jobSkillRepo = jobSkillRepo;
    }

    /**
     * Lấy Job theo ID kèm danh sách JobSkill
     */
    public Optional<Job> getById(Long id) {
        Optional<Job> job = repo.findById(id);
        job.ifPresent(j -> j.setSkills(jobSkillRepo.findByJobId(j.getId())));
        return job;
    }

    /**
     * Tạo Job kèm JobSkill
     */
    @Transactional
    public Job create(Job job, List<JobSkill> skills) {
        Job savedJob = repo.create(job);

        if (skills != null) {
            for (JobSkill js : skills) {
                js.setJobId(savedJob.getId());
                jobSkillRepo.create(js);
            }
            savedJob.setSkills(skills); // set vào object trả về
        }

        return savedJob;
    }

    /**
     * Cập nhật Job kèm JobSkill
     */
    @Transactional
    public Optional<Job> update(Job job, List<JobSkill> skills) {
        if (job.getId() == null) return Optional.empty();

        Optional<Job> updatedJob = repo.update(job);
        if (updatedJob.isEmpty()) return Optional.empty();

        if (skills != null) {
            // Xóa các JobSkill cũ
            jobSkillRepo.deleteByJobId(job.getId());

            // Thêm JobSkill mới
            for (JobSkill js : skills) {
                js.setJobId(job.getId());
                jobSkillRepo.create(js);
            }
            updatedJob.get().setSkills(skills);
        }

        return updatedJob;
    }

    /**
     * Xóa Job (kèm JobSkill do cascade)
     */
    @Transactional
    public int delete(Long id) {
        return repo.delete(id);
    }

    /**
     * Lấy danh sách Job kèm JobSkill
     */
    public Page<Job> getAll(Job filter, Pageable pageable) {
        Page<Job> jobs = repo.findAll(filter, pageable);
        for (Job job : jobs.getItems()) {
            job.setSkills(jobSkillRepo.findByJobId(job.getId()));
        }
        return jobs;
    }

    /**
     * Đếm số lượng job theo category
     */
    public List<CategoryJobCount> countJobsByCategory(Job filter) {
        return repo.countJobsByCategory(filter);
    }
}
