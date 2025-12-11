package com.example.jobportal.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkill {
    private Long id;
    private Long jobId;
    private Long skillId;
    private Short requiredLevel;
    private Short priority; //1=normal, 2=high
}
