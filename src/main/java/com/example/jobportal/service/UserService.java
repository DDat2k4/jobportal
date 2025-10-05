package com.example.jobportal.service;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Lấy thông tin user đầy đủ (profile + roles + permissions + tokens)
     */
    public Optional<UserDTO> getUserDetail(Long userId) {
        return userRepository.findById(userId);
    }
}
