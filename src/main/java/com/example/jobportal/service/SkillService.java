package com.example.jobportal.service;

import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository repo;

    public SkillService(SkillRepository repo) {
        this.repo = repo;
    }

    public Optional<Skill> get(Skill skill) {
        if (skill.getId() != null) {
            return repo.findById(skill.getId());
        } else if (skill.getName() != null && !skill.getName().isEmpty()) {
            return repo.findByName(skill.getName());
        }
        return Optional.empty();
    }

    public Skill create(Skill skill) {
        return repo.create(skill);
    }

    public Optional<Skill> update(Skill skill) {
        if (skill.getId() == null) {
            return Optional.empty();
        }
        return repo.update(skill.getId(), skill);
    }

    public int delete(Skill skill) {
        if (skill.getId() == null) return 0;
        return repo.delete(skill.getId());
    }

    public Page<Skill> getAll(Skill filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}
