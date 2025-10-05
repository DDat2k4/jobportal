package com.example.jobportal.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Job {
    private Long id;
    private Long companyId;
    private String title;
    private String description;
    private String requirements;
    private String salaryRange;
    private String location;
    private Long categoryId;
    private String type;
    private LocalDate deadline;
    private Short status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


