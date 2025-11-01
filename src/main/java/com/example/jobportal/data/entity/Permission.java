package com.example.jobportal.data.entity;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Permission {
    private Long id;
    private String code;
    private String description;
}
