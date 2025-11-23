package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Notification;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.NotificationService;
import com.example.jobportal.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    /** Lấy 1 notification theo id (admin hoặc user có quyền) */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<Notification> getById(@PathVariable Long id) {
        Optional<Notification> notification = service.getById(id);
        return notification.map(n -> ApiResponse.ok("Notification fetched successfully", n))
                .orElseGet(() -> ApiResponse.error("Notification not found"));
    }

    /** Lấy tất cả notification của current user, phân trang, filter isRead */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Page<Notification>> getAllForCurrentUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Notification filter = new Notification()
                .setUserId(currentUserId)
                .setIsRead(isRead);

        Pageable pageable = new Pageable(page, size);
        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        Page<Notification> result = service.getAll(filter, pageable);
        return ApiResponse.ok("Notifications fetched successfully", result);
    }

    /** Tạo notification (thường backend tạo khi có event) */
    @PostMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_CREATE')")
    public ApiResponse<Notification> create(@RequestBody Notification notification) {
        Notification created = service.create(notification);
        return ApiResponse.ok("Notification created successfully", created);
    }

    /** Cập nhật notification (admin hoặc owner) */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ApiResponse<Notification> update(@PathVariable Long id, @RequestBody Notification notification) {
        notification.setId(id);
        Notification updated = service.update(id, notification)
                .orElseThrow(() -> new RuntimeException("Notification not found or update failed"));
        return ApiResponse.ok("Notification updated successfully", updated);
    }

    /** Xóa notification */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("Notification deleted successfully", deleted);
    }

    /** Đánh dấu notification là đã đọc */
    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Notification> markAsRead(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Notification updated = service.markAsRead(id, currentUserId);
        return ApiResponse.ok("Notification marked as read", updated);
    }

    // Đánh dấu tất cả đã đọc
    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        service.markAllAsRead(userId);
        return ApiResponse.ok("All notifications marked as read", null);
    }

    // Lấy số notification chưa đọc
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        long count = service.countUnread(userId);
        return ApiResponse.ok("Unread count fetched", count);
    }
}
