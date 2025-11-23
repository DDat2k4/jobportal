package com.example.jobportal.service;

import com.example.jobportal.data.entity.Notification;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Optional<Notification> getById(Long id) {
        return repo.findById(id);
    }

    public Notification create(Notification notification) {
        return repo.create(notification);
    }

    public Optional<Notification> update(Long id, Notification notification) {
        return repo.update(id, notification);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Notification> getAll(Notification filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    public Notification markAsRead(Long id, Long userId) {
        Notification notif = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notif.getUserId().equals(userId)) {
            throw new RuntimeException("Cannot mark other user's notification as read");
        }

        return repo.markAsRead(id)
                .orElseThrow(() -> new RuntimeException("Failed to mark notification as read"));
    }

    public void markAllAsRead(Long userId) {
        repo.markAllAsRead(userId);
    }

    public long countUnread(Long userId) {
        return repo.countUnread(userId);
    }
}
