package com.example.jobportal.data.entity;

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
public class Application {
    private Long id;
    private Long jobId;
    private Long seekerId;
    private String coverLetter;
    private String resumeUrl;
    private String status;
    private String feedback;
    private LocalDateTime appliedAt;
}