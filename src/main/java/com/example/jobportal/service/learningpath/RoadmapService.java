package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.learningpath.LearningResource;
import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.data.pojo.JobSkillWithName;
import com.example.jobportal.data.pojo.learningpath.LearningPathStep;
import com.example.jobportal.data.pojo.learningpath.MatchResult;
import com.example.jobportal.data.pojo.learningpath.RoadmapResult;
import com.example.jobportal.repository.JobSkillRepository;
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

    public RoadmapService(MatchService matchService,
                          JobSkillRepository jobSkillRepository,
                          LearningResourceRepository learningResourceRepository,
                          RoadmapTemplateRepository roadmapTemplateRepository) {
        this.matchService = matchService;
        this.jobSkillRepository = jobSkillRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.roadmapTemplateRepository = roadmapTemplateRepository;
    }

    public RoadmapResult generateRoadmap(Long userId, Long jobId) {

        MatchResult match = matchService.matchUserToJob(userId, jobId).orElse(null);

        if (match == null)
            return RoadmapResult.empty(userId, jobId);

        List<LearningPathStep> allSteps = new ArrayList<>();

        for (String missingSkillName : match.getMissingSkills()) {
            JobSkillWithName skillWithName = jobSkillRepository.findByJobIdWithSkillName(jobId).stream()
                    .filter(js -> js.getSkillName().equalsIgnoreCase(missingSkillName))
                    .findFirst()
                    .orElse(null);

            if (skillWithName == null) continue;

            Long skillId = skillWithName.getSkillId();

            // Lấy template
            List<RoadmapTemplate> templates = roadmapTemplateRepository.findBySkillIdOrderByStepOrder(skillId);

            // Lấy resources
            List<LearningResource> resources = learningResourceRepository.findBySkillId(skillId);

            for (RoadmapTemplate template : templates) {
                LearningPathStep step = new LearningPathStep();
                step.setTitle(template.getTitle());
                step.setSkill(missingSkillName);
                step.setDuration(template.getDurationDays() + " days");
                step.setAction(template.getAction());

                // Gán tất cả resource liên quan skill (có thể lọc theo type nếu muốn)
                List<String> stepResources = resources.stream()
                        .map(LearningResource::getTitle)
                        .toList();
                step.setResources(stepResources);

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
                step.setResources(resources.stream().map(LearningResource::getTitle).toList());
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