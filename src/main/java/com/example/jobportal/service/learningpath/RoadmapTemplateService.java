package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.learningpath.RoadmapTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoadmapTemplateService {

    private final RoadmapTemplateRepository repo;

    public RoadmapTemplateService(RoadmapTemplateRepository repo) {
        this.repo = repo;
    }

    public Optional<RoadmapTemplate> get(Long id) {
        return repo.findById(id);
    }

    public List<RoadmapTemplate> getBySkillId(Long skillId) {
        return repo.findBySkillIdOrderByStepOrder(skillId);
    }

    public RoadmapTemplate create(RoadmapTemplate t) {
        return repo.create(t);
    }

    public Optional<RoadmapTemplate> update(Long id, RoadmapTemplate t) {
        return repo.update(id, t);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<RoadmapTemplate> getAll(RoadmapTemplate filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}