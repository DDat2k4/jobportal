package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.learningpath.LearningResource;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.learningpath.LearningResourceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LearningResourceService {

    private final LearningResourceRepository repo;

    public LearningResourceService(LearningResourceRepository repo) {
        this.repo = repo;
    }

    public Optional<LearningResource> get(Long id) {
        return repo.findById(id);
    }

    public Page<LearningResource> getAll(LearningResource filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    public LearningResource create(LearningResource r) {
        return repo.create(r);
    }

    public Optional<LearningResource> update(Long id, LearningResource r) {
        return repo.update(id, r);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public boolean existsBySkillAndUrl(Long skillId, String url) {
        return repo.existsBySkillIdAndUrl(skillId, url);
    }
}