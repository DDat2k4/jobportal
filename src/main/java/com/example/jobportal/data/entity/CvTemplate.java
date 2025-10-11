package com.example.jobportal.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jooq.JSONB;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CvTemplate {
    private Long id;
    private String code;           // Mã template (VD: SIMPLE_WHITE)
    private String name;           // Tên hiển thị
    private String previewUrl;     // Link ảnh xem trước
    private JSONB config;          // Cấu hình (font, layout,...)
    private LocalDateTime createdAt;
}
