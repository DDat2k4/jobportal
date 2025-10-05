package com.example.jobportal.data.entity;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Role {
    private Long id;
    private String name;
    private String description;
}
