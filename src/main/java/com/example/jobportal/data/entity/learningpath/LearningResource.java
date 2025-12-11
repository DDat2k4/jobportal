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
public class LearningResource {
    private Long id;
    private Long skillId;
    private String title;
    private String url;
    private String type;
    private Integer difficulty;
    private Integer durationMinutes;
    private String provider;
    private LocalDateTime createdAt;
}

