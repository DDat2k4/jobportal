package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.learningpath.RoadmapTemplateRepository;
import com.example.jobportal.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoadmapTemplateService {

    private final RoadmapTemplateRepository repo;
    private final SkillService skillService;

    public RoadmapTemplateService(RoadmapTemplateRepository repo, SkillService skillService) {
        this.repo = repo;
        this.skillService = skillService;
    }

    public Optional<RoadmapTemplate> get(Long id) {
        return repo.findById(id);
    }

    public List<RoadmapTemplate> getBySkillName(Long skillId) {
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

    public List<RoadmapTemplate> getBySkillName(String skillName) {
        Skill skill = skillService.findByNameIgnoreCase(skillName)
                .orElseThrow(() -> new RuntimeException("Skill not found: " + skillName));

        return repo.findBySkillIdOrderByStepOrder(skill.getId());
    }
}