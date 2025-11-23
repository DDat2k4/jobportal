package com.example.jobportal.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {
    private final SimpMessagingTemplate template;

    public WebSocketNotificationService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendToUser(Long userId, Object payload) {
        template.convertAndSendToUser(
                String.valueOf(userId),
                "/notifications",
                payload
        );
    }
}

