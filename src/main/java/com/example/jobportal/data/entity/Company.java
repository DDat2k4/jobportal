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
public class Company {
    private Long id;
    private String name;
    private String tagline;
    private String ownerName;
    private String logoUrl;
    private Long categoryId;
    private Integer establishedYear;
    private Integer employees;
    private String workingTime;
    private String description;
    private String website;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

