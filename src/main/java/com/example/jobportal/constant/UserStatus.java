package com.example.jobportal.constant;

import lombok.Data;

@Data
public class UserStatus {
    public static final short INACTIVE = 0;
    public static final short ACTIVE = 1;
    public static final short LOCKED = 3;
}
