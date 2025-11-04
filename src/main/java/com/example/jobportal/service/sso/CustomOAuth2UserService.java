package com.example.jobportal.service.sso;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.UserDetailResponse;
import com.example.jobportal.mapper.UserMapper;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.repository.UserTokenRepository;
import com.example.jobportal.service.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = (String) oauth2User.getAttributes().get("email");
        String tmpUsername = email != null ? email : "fb-" + oauth2User.getAttributes().get("id");

        // Provision local user
        Optional<UserDTO> optUser = userRepository.findByUsername(tmpUsername);

        UserDTO user = optUser.orElseGet(() -> {
            String randomPassword = UUID.randomUUID().toString();
            userRepository.create(tmpUsername, email, passwordEncoder.encode(randomPassword));
            return userRepository.findByUsername(tmpUsername)
                    .orElseThrow(() -> new RuntimeException("Failed to create local user"));
        });

        // Update last login
        userRepository.updateLastLogin(user.getId());

        // Revoke old tokens
        userTokenRepository.findActiveTokensByUserId(user.getId()).forEach(t ->
                userTokenRepository.revokeToken(t.getId())
        );

        // Get roles & permissions
        UserDTO userDetail = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User detail not found"));

        UserDetailResponse userResponse = UserMapper.toResponse(userDetail);

        // Generate new tokens (⚙️ cập nhật theo JwtService mới)
        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                userResponse.getRoles(),
                userResponse.getPermissions()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        // Lưu refresh token vào DB
        userTokenRepository.insertToken(user.getId(), refreshToken);

        // Merge OAuth2 attributes + local tokens + roles/permissions
        Map<String, Object> attrs = new HashMap<>(oauth2User.getAttributes());
        attrs.put("localUserId", user.getId());
        attrs.put("localUsername", user.getUsername());
        attrs.put("localAccessToken", accessToken);
        attrs.put("localRefreshToken", refreshToken);
        attrs.put("roles", userResponse.getRoles());
        attrs.put("permissions", userResponse.getPermissions());

        return new DefaultOAuth2User(oauth2User.getAuthorities(), attrs, "id");
    }
}
