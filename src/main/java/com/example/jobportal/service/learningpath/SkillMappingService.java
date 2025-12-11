package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.learningpath.SkillMapping;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.learningpath.SkillMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillMappingService {

    private final SkillMappingRepository repo;

    public SkillMappingService(SkillMappingRepository repo) {
        this.repo = repo;
    }

    public Optional<SkillMapping> get(Long id) {
        return repo.findById(id);
    }

    public Optional<SkillMapping> getByAlias(String alias) {
        return repo.findByAlias(alias);
    }

    public List<SkillMapping> getBySkillId(Long skillId) {
        return repo.findBySkillId(skillId);
    }

    public SkillMapping create(SkillMapping m) {
        return repo.create(m);
    }

    public Optional<SkillMapping> update(Long id, SkillMapping m) {
        return repo.update(id, m);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<SkillMapping> getAll(SkillMapping filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}