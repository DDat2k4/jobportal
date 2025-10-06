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
}
