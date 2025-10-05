package com.example.jobportal.data.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class UserProfile {
    private Long id;
    private Long userId;
    private String name;
    private String avatar;
    private Short gender;
    private LocalDate birthDate;
    private String headline;
    private String note;
}
