package com.example.jobportal.data.pojo.learningpath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathStep {
    private String title;
    private String skill; // tên skill mục tiêu
    private String duration; // ví dụ "1 week"
    private List<String> resources;
    private String action; // hành động đề xuất (build project, quiz...)
}