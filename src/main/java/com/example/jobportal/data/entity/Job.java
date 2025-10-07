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
    private String type;        //FULLTIME, PARTTIME,...
    private LocalDate deadline;
    /**
     * Trạng thái (status) của tin tuyển dụng.
     *
     * 0 = DRAFT      → Tin đang soạn, chưa công khai
     * 1 = ACTIVE     → Tin đang hiển thị, ứng viên có thể ứng tuyển
     * 2 = PAUSED     → Tạm dừng hiển thị, chưa nhận thêm ứng viên
     * 3 = EXPIRED    → Hết hạn (deadline đã qua)
     * 4 = CLOSED     → Đã đóng bởi nhà tuyển dụng
     * 5 = DELETED    → Đã xóa logic, ẩn khỏi hệ thống
     */
    private Short status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


