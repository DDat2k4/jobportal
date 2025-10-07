package com.example.jobportal.service;

import com.example.jobportal.data.entity.Application;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository repo;

    public ApplicationService(ApplicationRepository repo) {
        this.repo = repo;
    }

    public Optional<Application> getById(Long id) {
        return repo.findById(id);
    }

    public Application create(Application app) {
        app.setStatus("PENDING");
        return repo.create(app);
    }

    public Optional<Application> update(Long id, Application application) {
        return repo.update(id, application);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Application> getAll(Application filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    // Bản đồ quy tắc chuyển trạng thái
    private static final Map<String, List<String>> TRANSITIONS = Map.of(
            "PENDING", List.of("APPROVED", "REJECTED"),
            "APPROVED", List.of("INTERVIEW", "HIRED"),
            "INTERVIEW", List.of("HIRED", "REJECTED")
    );

    public Application changeStatus(Long id, String newStatus, String feedback) {
        Application app = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        String current = app.getStatus();
        List<String> allowed = TRANSITIONS.getOrDefault(current, Collections.emptyList());

        if (!allowed.contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Cannot change status from %s to %s", current, newStatus));
        }

        app.setStatus(newStatus);
        app.setFeedback(feedback);
        return repo.update(id, app)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed"));
    }
}

