package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.JobSkill;
import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.data.pojo.learningpath.LearningPathStep;
import com.example.jobportal.data.pojo.learningpath.RoadmapEngine;
import com.example.jobportal.repository.SkillRepository;
import com.example.jobportal.repository.learningpath.LearningResourceRepository;
import com.example.jobportal.repository.learningpath.RoadmapTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseRoadmapEngine implements RoadmapEngine {

    private final SkillRepository skillRepo;
    private final LearningResourceRepository resRepo;
    private final RoadmapTemplateRepository templateRepo;

    public DatabaseRoadmapEngine(SkillRepository skillRepo,
                                 LearningResourceRepository resRepo,
                                 RoadmapTemplateRepository templateRepo) {
        this.skillRepo = skillRepo;
        this.resRepo = resRepo;
        this.templateRepo = templateRepo;
    }

    @Override
    public List<LearningPathStep> buildForSkill(String skillName, JobSkill required) {

        String normalized = skillName.toLowerCase().trim();

        Skill skill = skillRepo.findByNormalizedName(normalized)
                .orElseGet(() -> skillRepo.findByName(skillName).orElse(null));

        if (skill == null)
            return List.of(defaultStep(skillName));

        var resources = resRepo.findBySkillId(skill.getId());
        var templates = templateRepo.findBySkillIdOrderByStepOrder(skill.getId());

        List<LearningPathStep> steps = new ArrayList<>();

        for (var t : templates) {

            List<String> res = resources.stream()
                    .filter(r -> r.getDifficulty() <= skill.getDifficulty())
                    .map(r -> r.getTitle() + " (" + r.getProvider() + ")")
                    .toList();

            steps.add(LearningPathStep.builder()
                    .skill(skill.getName())
                    .title(t.getTitle())
                    .duration(t.getDurationDays() + " days")
                    .action(t.getAction())
                    .resources(res)
                    .build());
        }

        return steps;
    }

    private LearningPathStep defaultStep(String name) {
        return LearningPathStep.builder()
                .skill(name)
                .title("Learn " + name)
                .duration("7 days")
                .action("Take an intro course + build a mini project")
                .resources(List.of(
                        "Basic course: " + name,
                        "Practice project with " + name
                ))
                .build();
    }
}
