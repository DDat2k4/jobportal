package com.example.jobportal.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jooq.JSONB;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UserCv {
    private Long id;
    private Long userId;
    private String title;                       // Tên CV (VD: "CV Backend Developer")
    private String templateCode;                // Mã template nếu có (VD: "MODERN_BLUE")
    private String summary;                     // Tóm tắt bản thân
    private Map<String, Object> data;           // Dữ liệu CV động (học vấn, kỹ năng,...)
    private Boolean isDefault;                  // Đánh dấu CV mặc định
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
