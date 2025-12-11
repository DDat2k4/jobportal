package com.example.jobportal.data.pojo;

import lombok.Data;

@Data
public class JobSkillWithName {
    private Long id;
    private Long jobId;
    private Long skillId;
    private String skillName;
    private Short requiredLevel;
    private Short priority;
}

