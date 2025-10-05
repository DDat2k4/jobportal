package com.example.jobportal.service.sso;

import com.example.jobportal.data.response.AuthResponse;
import com.example.jobportal.repository.UserProfileRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.UserService;
import com.example.jobportal.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        log.info("OAuth2 login success: {}", authentication.getName());

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2User oauth2User)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("name");
        String avatar = oauth2User.getAttribute("picture");
        String provider = detectProvider(oauth2User);

        if (email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Lấy hoặc tạo user bằng jOOQ
        Long userId = userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseGet(() -> {
                    String finalUsername = (username != null) ? username : email.split("@")[0];
                    // Tạo user mới
                    Long id = userRepository.insertUser(finalUsername, email, ""); // password rỗng
                    // Tạo profile
                    userProfileRepository.insert(id, finalUsername, avatar);
                    return id;
                });

        // Update profile nếu đã tồn tại
        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            userProfileRepository.update(userId, username, avatar);
        });

        // Lấy username thực tế
        String finalUsername = username != null ? username : email.split("@")[0];

        // Lấy roles & permissions
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        userService.getUserDetail(userId).ifPresent(detail -> {
            if (detail.getRoles() != null) roles.addAll(detail.getRoles());
            if (detail.getPermissions() != null) permissions.addAll(detail.getPermissions());
        });

        // Sinh JWT
        String accessToken = jwtService.generateToken(finalUsername, roles, permissions);
        String refreshToken = jwtService.generateRefreshToken(finalUsername);

        // Trả về AuthResponse
        AuthResponse authResponse = AuthResponse.builder()
                .username(finalUsername)
                .email(email)
                .avatar(avatar)
                .provider(provider)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .permissions(permissions)
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), authResponse);
    }

    private String detectProvider(OAuth2User user) {
        if (user.getAttributes().containsKey("sub")) return "google";
        if (user.getAttributes().containsKey("id")) return "facebook";
        return "unknown";
    }
}
