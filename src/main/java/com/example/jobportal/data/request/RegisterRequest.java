package com.example.jobportal.data.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String phone;
    private String password;
    private String roleType;
}

