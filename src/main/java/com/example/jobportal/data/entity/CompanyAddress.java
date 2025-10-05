package com.example.jobportal.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CompanyAddress {
    private Long id;
    private Long companyId;
    private String email;
    private String phone;
    private String landline;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private LocalDateTime createdAt;
}