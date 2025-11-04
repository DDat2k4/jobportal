package com.example.jobportal.service.sso;

import com.example.jobportal.constant.RoleConstant;
import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.repository.UserTokenRepository;
import com.example.jobportal.service.JwtService;
import com.example.jobportal.service.UserService;
import com.example.jobportal.util.CommonUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String username = Optional.ofNullable(email).orElse("user-" + UUID.randomUUID());

        // --- Tìm hoặc tạo user
        UserDTO userDto = userRepository.findByEmail(email).orElseGet(() -> {
            String randomPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
            Long userId = userRepository.create(username, email, randomPasswordHash);

            // Tạo profile mặc định
            userRepository.createProfile(userId, username, null);

            // Gán role mặc định
            long roleId = RoleConstant.ROLE_EMPLOYER; // fallback (2)

            // Lấy roleType nếu có truyền từ FE (VD: Google OAuth2 redirect có param roleType)
            String requestedRole = (String) userRequest.getAdditionalParameters().get("roleType");
            long parsedRole = CommonUtils.toLong(requestedRole, roleId);

            // Chỉ cho phép role 2 (EMPLOYER) hoặc 3 (JOB_SEEKER)
            if (parsedRole == RoleConstant.ROLE_EMPLOYER || parsedRole == RoleConstant.ROLE_JOB_SEEKER) {
                roleId = parsedRole;
            } else {
                System.out.printf("Invalid roleType from SSO: %s -> fallback to EMPLOYER%n", requestedRole);
            }

            // Gán role
            userRepository.assignRole(userId, roleId);


            return userRepository.findById(userId).orElseThrow();
        });

        // --- Cập nhật last login
        userRepository.updateLastLogin(userDto.getId());

        // --- Revoke token cũ
        userTokenRepository.revokeAllTokensByUserId(userDto.getId());

        // --- Lấy chi tiết user (roles, permissions)
        UserDTO detailDto = userService.getUserDetail(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User detail not found"));

        // --- Sinh JWT mới
        String accessToken = jwtService.generateToken(userDto.getId(), userDto.getUsername(), detailDto.getRoles(), detailDto.getPermissions());
        String refreshToken = jwtService.generateRefreshToken(userDto.getId(), userDto.getUsername());
        userTokenRepository.insertToken(userDto.getId(), refreshToken);

        // --- Attach attributes vào OIDC
        Map<String, Object> attrs = new HashMap<>(oidcUser.getAttributes());
        attrs.put("localAccessToken", accessToken);
        attrs.put("localRefreshToken", refreshToken);
        attrs.put("localUsername", userDto.getUsername());
        attrs.put("roles", detailDto.getRoles());
        attrs.put("permissions", detailDto.getPermissions());

        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"
        ) {
            @Override
            public Map<String, Object> getAttributes() {
                return Collections.unmodifiableMap(attrs);
            }
        };
    }
}
