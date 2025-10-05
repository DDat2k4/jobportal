package com.example.jobportal.data.entity;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRole {
    private Long id;
    private Long userId;
    private Long roleId;
}


