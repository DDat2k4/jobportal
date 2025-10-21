package com.example.jobportal.controller;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String role,  // "EMPLOYER", "JOB_SEEKER"
            Pageable pageable
    ) {
        var result = userService.getUsersByRole(role, pageable);
        return ApiResponse.ok( "Result page UserDTO successful",result);
    }
}
