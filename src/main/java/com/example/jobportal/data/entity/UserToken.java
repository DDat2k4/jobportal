package com.example.jobportal.data.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserToken {
    private Long id;
    private Long userId;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean revoked;
}

