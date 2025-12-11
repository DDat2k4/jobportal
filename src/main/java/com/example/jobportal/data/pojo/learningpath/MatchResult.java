package com.example.jobportal.data.pojo.learningpath;

import com.example.jobportal.data.entity.JobSkill;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchResult {
    private Long userId;
    private Long jobId;

    // scores 0-100
    private int totalScore;
    private int skillScore;
    private int experienceScore;
    private int educationScore;

    private List<String> matchedSkills;
    private List<String> missingSkills;

    private List<JobSkill> matchedJobSkills;
    private List<JobSkill> missingJobSkills;
}