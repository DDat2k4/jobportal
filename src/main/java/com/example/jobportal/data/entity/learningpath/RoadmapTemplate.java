package com.example.jobportal.data.entity.learningpath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RoadmapTemplate {
    private Long id;
    private Long skillId;
    private Integer stepOrder;
    private String title;
    private String action;
    private Integer durationDays;
    private LocalDateTime createdAt;
}

