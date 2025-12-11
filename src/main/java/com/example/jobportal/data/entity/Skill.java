package com.example.jobportal.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private Long id;
    private String name;
    private String type;
    private String normalizedName;    // "spring boot" / "reactjs"
    private Integer difficulty;         // 1=easy, 2=medium, 3=hard
    private List<String> aliases;     // JSOB
    private String description;
    private LocalDateTime createdAt;
}