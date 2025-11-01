package com.example.jobportal.data.entity;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RolePermission {
    private Long id;
    private Long roleId;
    private Long permissionId;
}
