package com.example.jobportal.service;

import com.example.jobportal.data.entity.Application;
import com.example.jobportal.data.entity.Job;
import com.example.jobportal.data.entity.Notification;
import com.example.jobportal.repository.ApplicationRepository;
import com.example.jobportal.repository.EmployerCompanyRepository;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
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
    private final NotificationService notificationService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final EmployerCompanyRepository employerCompanyRepository;
    private final JobService jobService;

    public ApplicationService(ApplicationRepository repo,
                              NotificationService notificationService,
                              WebSocketNotificationService webSocketNotificationService,
                              EmployerCompanyRepository employerCompanyRepository,
                              JobService jobService) {
        this.repo = repo;
        this.notificationService = notificationService;
        this.webSocketNotificationService = webSocketNotificationService;
        this.employerCompanyRepository = employerCompanyRepository;
        this.jobService = jobService;
    }

    public Optional<Application> getById(Long id) {
        return repo.findById(id);
    }

    public Application create(Application app) {
        app.setStatus("PENDING");
        Application created = repo.create(app);
        Job job = jobService.getById(created.getJobId()).orElseThrow(()-> new RuntimeException("Job not found") );
        // Thông báo cho người ứng tuyển
        notifyUsers(List.of(created.getSeekerId()), "You applied for job: " + created.getJobId());

        // Lấy tất cả employer liên quan đến job
        List<Long> employerIds = employerCompanyRepository.findEmployerIdsByJobId(created.getJobId());
        notifyUsers(employerIds, "Job " + job.getTitle() + " has a new application.");

        return created;
    }

    public Optional<Application> update(Long id, Application application) {
        return repo.update(id, application);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Application> getAll(Application filter, Pageable pageable, List<Long> companyIds) {
        return repo.findAll(filter, companyIds, pageable);
    }

    private static final Map<String, List<String>> TRANSITIONS = Map.of(
            "PENDING", List.of("APPROVED", "REJECTED", "CANCELED"),
            "APPROVED", List.of("INTERVIEW", "REJECTED", "CANCELED"),
            "INTERVIEW", List.of("HIRED", "REJECTED", "CANCELED"),
            "HIRED", List.of(),
            "REJECTED", List.of(),
            "CANCELED", List.of()
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
        Application updated = repo.update(id, app)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed"));

        notifyUsers(List.of(updated.getSeekerId()), "Application status changed: " + newStatus + ". Feedback: " + feedback);

        if (newStatus.equals("APPROVED") || newStatus.equals("INTERVIEW")) {
            List<Long> employerIds = employerCompanyRepository.findEmployerIdsByJobId(updated.getJobId());
            notifyUsers(employerIds, "Application status changed: " + newStatus);
        }

        return updated;
    }

    private void notifyUsers(List<Long> userIds, String message) {
        for (Long userId : userIds) {
            Notification notif = new Notification();
            notif.setUserId(userId);
            notif.setMessage(message);
            notif.setIsRead(false);
            notificationService.create(notif);
            webSocketNotificationService.sendToUser(userId, notif);
        }
    }
}
