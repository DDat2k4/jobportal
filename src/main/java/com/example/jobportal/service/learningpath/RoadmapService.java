package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.JobSkill;
import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.data.entity.learningpath.LearningResource;
import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.data.pojo.JobSkillWithName;
import com.example.jobportal.data.pojo.learningpath.LearningPathStep;
import com.example.jobportal.data.pojo.learningpath.MatchResult;
import com.example.jobportal.data.pojo.learningpath.RoadmapResult;
import com.example.jobportal.repository.JobSkillRepository;
import com.example.jobportal.repository.SkillRepository;
import com.example.jobportal.repository.learningpath.LearningResourceRepository;
import com.example.jobportal.repository.learningpath.RoadmapTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoadmapService {

    private final MatchService matchService;
    private final JobSkillRepository jobSkillRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final RoadmapTemplateRepository roadmapTemplateRepository;
    private final SkillRepository skillRepository;

    public RoadmapService(MatchService matchService,
                          JobSkillRepository jobSkillRepository,
                          LearningResourceRepository learningResourceRepository,
                          RoadmapTemplateRepository roadmapTemplateRepository,
                          SkillRepository skillRepository) {
        this.matchService = matchService;
        this.jobSkillRepository = jobSkillRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.roadmapTemplateRepository = roadmapTemplateRepository;
        this.skillRepository = skillRepository;
    }

    public RoadmapResult generateRoadmap(Long userId, Long jobId) {

        MatchResult match = matchService.matchUserToJob(userId, jobId).orElse(null);
        if (match == null) {
            return RoadmapResult.empty(userId, jobId);
        }

        List<LearningPathStep> allSteps = new ArrayList<>();

        for (JobSkill js : match.getMissingJobSkills()) {

            Long skillId = js.getSkillId();

            String skillName = skillRepository.findById(skillId)
                    .map(Skill::getName)
                    .orElse("Unknown skill");

            List<RoadmapTemplate> templates =
                    roadmapTemplateRepository.findBySkillIdOrderByStepOrder(skillId);

            List<LearningResource> resources =
                    learningResourceRepository.findBySkillId(skillId);

            for (RoadmapTemplate template : templates) {

                LearningPathStep step = new LearningPathStep();
                step.setSkill(skillName);
                step.setTitle(template.getTitle());
                step.setAction(template.getAction());
                step.setDuration(template.getDurationDays() + " days");
                step.setResources(resources);

                allSteps.add(step);
            }
        }

        return RoadmapResult.builder()
                .userId(userId)
                .jobId(jobId)
                .missingSkills(match.getMissingSkills())
                .steps(allSteps)
                .build();
    }

    public List<RoadmapResult> generateRoadmapsForJob(Long jobId, List<MatchResult> matches) {
        List<RoadmapResult> results = new ArrayList<>();
        for (MatchResult match : matches) {
            results.add(generateRoadmapFromMatch(match, jobId));
        }
        return results;
    }

    private RoadmapResult generateRoadmapFromMatch(MatchResult match, Long jobId) {
        List<LearningPathStep> allSteps = new ArrayList<>();
        for (String missingSkillName : match.getMissingSkills()) {
            JobSkillWithName skillWithName = jobSkillRepository.findByJobIdWithSkillName(jobId).stream()
                    .filter(js -> js.getSkillName().equalsIgnoreCase(missingSkillName))
                    .findFirst()
                    .orElse(null);
            if (skillWithName == null) continue;

            Long skillId = skillWithName.getSkillId();
            List<RoadmapTemplate> templates = roadmapTemplateRepository.findBySkillIdOrderByStepOrder(skillId);
            List<LearningResource> resources = learningResourceRepository.findBySkillId(skillId);

            for (RoadmapTemplate template : templates) {
                LearningPathStep step = new LearningPathStep();
                step.setTitle(template.getTitle());
                step.setSkill(missingSkillName);
                step.setDuration(template.getDurationDays() + " days");
                step.setAction(template.getAction());
                step.setResources(resources);
                allSteps.add(step);
            }
        }
        return RoadmapResult.builder()
                .userId(match.getUserId())
                .jobId(jobId)
                .missingSkills(match.getMissingSkills())
                .steps(allSteps)
                .build();
    }
}