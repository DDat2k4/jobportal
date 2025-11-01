package com.example.jobportal.config.sso;

import com.example.jobportal.constant.RoleConstant;
import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.AuthResponse;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.repository.UserTokenRepository;
import com.example.jobportal.service.JwtService;
import com.example.jobportal.service.UserService;
import com.example.jobportal.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserTokenRepository userTokenRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = oidcUser.getEmail();
        String username = (String) oidcUser.getAttributes().get("name");
        String avatar = (String) oidcUser.getAttributes().get("picture");
        String provider = detectProvider(oidcUser);

        log.info("OAuth2 login success: {} via {}", email, provider);

        // --- Lấy hoặc tạo user qua repository
        UserDTO user = userRepository.findByEmail(email).orElseGet(() -> {
            String defaultUsername = username != null ? username : email.split("@")[0];
            String randomPasswordHash = UUID.randomUUID().toString(); // chỉ để lưu hash

            Long userId = userRepository.createUser(defaultUsername, email, randomPasswordHash);

            // Nếu cần tạo profile và role mặc định
            userRepository.createProfile(userId, defaultUsername, avatar);
            // Nếu FE có gửi roleType thì lấy ra
            String requestedRole = request.getParameter("roleType");

            // Chỉ chấp nhận 2 hoặc 3, mặc định là 2
            long defaultRoleId = RoleConstant.ROLE_EMPLOYER; // fallback = 2
            long parsedRole = CommonUtils.toLong(requestedRole, defaultRoleId);

            if (parsedRole == RoleConstant.ROLE_EMPLOYER || parsedRole == RoleConstant.ROLE_JOB_SEEKER) {
                defaultRoleId = parsedRole;
            } else {
                log.warn("Invalid roleType received from OAuth2: {} → fallback to EMPLOYER", requestedRole);
            }

            userRepository.assignRole(userId, defaultRoleId);

            return userRepository.findById(userId).orElseThrow();
        });

        // Cập nhật last login
        userRepository.updateLastLogin(user.getId());

        // Thu hồi refresh token cũ
        userTokenRepository.revokeAllTokensByUserId(user.getId());

        // Lấy chi tiết roles & permissions
        UserDTO detail = userService.getUserDetail(user.getId())
                .orElseThrow(() -> new RuntimeException("User detail not found"));

        // Tạo JWT access + refresh token
        String accessToken = jwtService.generateToken(user.getId() ,user.getUsername(), detail.getRoles(), detail.getPermissions());
        String refreshToken = jwtService.generateRefreshToken(user.getId() ,user.getUsername());

        // Lưu refresh token vào DB
        userTokenRepository.insertToken(user.getId(), refreshToken);

        // Tạo response trả SPA
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUsername(user.getUsername());
        authResponse.setEmail(user.getEmail());
        authResponse.setAvatar(avatar);
        authResponse.setProvider(provider);
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setRoles(detail.getRoles());
        authResponse.setPermissions(detail.getPermissions());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        new ObjectMapper().writeValue(response.getWriter(), authResponse);
    }

    private String detectProvider(OidcUser oidcUser) {
        if (oidcUser.getAttributes().containsKey("sub")) return "google";
        if (oidcUser.getAttributes().containsKey("id")) return "facebook";
        return "unknown";
    }
}
