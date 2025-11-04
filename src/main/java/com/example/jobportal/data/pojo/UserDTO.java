package com.example.jobportal.data.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<Long> companyIds;
    private String phone;
    private String passwordHash;
    private Integer failedAttempts;
    private LocalDateTime lastLogin;
    private LocalDateTime lockedUntil;
    private String name;
    private String avatar;
    private Set<String> roles;
    private Set<String> permissions;
    // 1 → ACTIVE, 0 → INACTIVE, 3 → LOCKED
    private Short active;
    private List<String> activeTokens;
}
