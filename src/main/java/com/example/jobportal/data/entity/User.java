package com.example.jobportal.data.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private UUID uid;
    private String username;
    private String email;
    private String phone;
    private String passwordHash;
    private Short active;
    private Integer failedAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private LocalDateTime lockedUntil;
}