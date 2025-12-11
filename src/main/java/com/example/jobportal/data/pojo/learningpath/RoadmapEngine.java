package com.example.jobportal.data.pojo.learningpath;

import com.example.jobportal.data.entity.JobSkill;

import java.util.List;

public interface RoadmapEngine {
    List<LearningPathStep> buildForSkill(String skillName, JobSkill required);
}