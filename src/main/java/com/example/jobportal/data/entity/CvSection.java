package com.example.jobportal.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jooq.JSONB;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class CvSection {
    private Long id;
    private Long cvId;
    private String type;                     // Loại block (EDUCATION, EXPERIENCE,...)
    private String title;                    // Tiêu đề hiển thị
    private Map<String, Object> content;     // Nội dung JSON động
    private Integer position;                // Thứ tự hiển thị
    private LocalDateTime createdAt;
}
