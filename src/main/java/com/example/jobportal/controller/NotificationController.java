package com.example.jobportal.controller;

import com.example.jobportal.data.entity.Notification;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<Notification> getById(@PathVariable Long id) {
        Optional<Notification> notification = service.getById(id);
        return notification.map(n -> ApiResponse.ok("Notification fetched successfully", n))
                .orElseGet(() -> ApiResponse.error("Notification not found"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<Page<Notification>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean asc
    ) {
        Pageable pageable = new Pageable(page, size);

        // Sắp xếp động
        if (sortBy != null) {
            pageable.addOrder(sortBy, asc != null && asc ? Order.Direction.ASC : Order.Direction.DESC);
        } else {
            pageable.setDefaultSort("id");
        }

        // Lọc động
        Notification filter = new Notification()
                .setUserId(userId)
                .setIsRead(isRead);

        Page<Notification> result = service.getAll(filter, pageable);
        return ApiResponse.ok("Notifications fetched successfully", result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_CREATE')")
    public ApiResponse<Long> create(@RequestBody Notification notification) {
        Long id = service.create(notification);
        return ApiResponse.ok("Notification created successfully", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody Notification notification) {
        notification.setId(id);
        int updated = service.update(id, notification);
        return ApiResponse.ok("Notification updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_DELETE')")
    public ApiResponse<Integer> delete(@PathVariable Long id) {
        int deleted = service.delete(id);
        return ApiResponse.ok("Notification deleted successfully", deleted);
    }
}
