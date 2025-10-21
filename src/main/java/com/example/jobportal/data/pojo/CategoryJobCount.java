package com.example.jobportal.data.pojo;

import lombok.Data;

@Data
public class CategoryJobCount {
    private Long categoryId;
    private String categoryName;
    private Long jobCount;
}
