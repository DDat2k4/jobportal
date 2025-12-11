package com.example.jobportal.data.pojo.learningpath;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoadmapResult {
    private Long userId;
    private Long jobId;
    private List<String> missingSkills;
    private List<LearningPathStep> steps;

    public static RoadmapResult empty(Long userId, Long jobId) {
        return RoadmapResult.builder()
                .userId(userId)
                .jobId(jobId)
                .missingSkills(List.of())
                .steps(List.of())
                .build();
    }
}