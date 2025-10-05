package com.example.jobportal.mapper;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.UserDetailResponse;

public class UserMapper {
    public static UserDetailResponse toResponse(UserDTO dto) {
        return new UserDetailResponse(
                dto.getId(),
                dto.getUsername(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getLastLogin(),
                dto.getName(),
                dto.getAvatar(),
                dto.getRoles(),
                dto.getPermissions(),
                dto.getActiveTokens()
        );
    }
}


