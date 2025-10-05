package com.example.jobportal.data.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class EmployerCompanyId implements Serializable {
    private Long employer;
    private Long company;
}
