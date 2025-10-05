package com.example.jobportal.service.sso;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.UserDetailResponse;
import com.example.jobportal.mapper.UserMapper;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.repository.UserTokenRepository;
import com.example.jobportal.service.JwtService;
import com.example.jobportal.service.UserService;
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

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = (String) oauth2User.getAttributes().get("email");
        String tmpUsername = email != null ? email : "fb-" + oauth2User.getAttributes().get("id");

        // 1. Provision local user
        Optional<UserDTO> optUser = userRepository.findByUsername(tmpUsername);

        UserDTO user = optUser.orElseGet(() -> {
            String randomPassword = UUID.randomUUID().toString();
            userRepository.createUser(tmpUsername, email, passwordEncoder.encode(randomPassword));
            return userRepository.findByUsername(tmpUsername)
                    .orElseThrow(() -> new RuntimeException("Failed to create local user"));
        });

        // 2. Update last login
        userRepository.updateLastLogin(user.getId());

        // 3. Revoke old tokens
        userTokenRepository.findActiveTokensByUserId(user.getId()).forEach(t ->
                userTokenRepository.revokeToken(t.getId())
        );

        // 4. Get roles & permissions
        UserDTO userDetail = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User detail not found"));

        UserDetailResponse userResponse = UserMapper.toResponse(userDetail);

        // 5. Generate new tokens
        String accessToken = jwtService.generateToken(
                user.getUsername(),
                userResponse.getRoles(),
                userResponse.getPermissions()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        userTokenRepository.insertToken(user.getId(), refreshToken);

        // 6. Merge OAuth2 attributes + local tokens + roles/permissions
        Map<String, Object> attrs = new HashMap<>(oauth2User.getAttributes());
        attrs.put("localAccessToken", accessToken);
        attrs.put("localRefreshToken", refreshToken);
        attrs.put("localUsername", user.getUsername());
        attrs.put("roles", userResponse.getRoles());
        attrs.put("permissions", userResponse.getPermissions());

        return new DefaultOAuth2User(oauth2User.getAuthorities(), attrs, "id");
    }
}
