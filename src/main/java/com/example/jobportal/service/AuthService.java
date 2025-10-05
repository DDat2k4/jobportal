package com.example.jobportal.service;

import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.data.response.AuthResponse;
import com.example.jobportal.data.response.UserDetailResponse;
import com.example.jobportal.data.request.AuthProperties;
import com.example.jobportal.exception.AuthException;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.repository.UserTokenRepository;
import com.example.jobportal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties authProperties;
    private final UserService userService;

    // LOGIN
    public AuthResponse login(String username, String rawPassword) {
        UserDTO user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("User not found"));

        // Check locked
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AuthException("Account is locked until " + user.getLockedUntil());
        }

        // Check password
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            handleFailedAttempt(user);
            throw new AuthException("Invalid credentials");
        }

        // Success
        resetLoginAttempts(user);
        log.info("User {} logged in successfully", username);
        return generateTokens(user);
    }

    // REFRESH TOKEN
    public AuthResponse refreshToken(String refreshToken) {
        var token = userTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new AuthException("Refresh token not found or revoked"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("Refresh token expired");
        }

        UserDTO user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new AuthException("User not found"));

        log.info("Refresh token used for user {}", user.getUsername());

        UserDTO userDetail = userRepository.findById(user.getId())
                .orElseThrow(() -> new AuthException("User detail not found"));

        String newAccessToken = jwtService.generateToken(
                user.getUsername(),
                userDetail.getRoles(),
                userDetail.getPermissions()
        );

        return AuthResponse.builder()
                .username(user.getUsername())
                .email(userDetail.getEmail())
                .avatar(userDetail.getAvatar())
                .provider("local")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .roles(userDetail.getRoles())
                .permissions(userDetail.getPermissions())
                .build();
    }

    // LOGOUT 1 device
    public void logout(String refreshToken) {
        var token = userTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new AuthException("Refresh token not found or already revoked"));

        userTokenRepository.revokeToken(token.getId());
        log.info("Refresh token revoked for userId={}", token.getUserId());
    }

    // LOGOUT ALL DEVICES
    public void logoutAll(Long userId) {
        userTokenRepository.revokeAllTokensByUserId(userId);
        log.info("All refresh tokens revoked for userId={}", userId);
    }

    // CHANGE PASSWORD
    public void changePassword(String username, String oldPassword, String newPassword) {
        UserDTO user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new AuthException("Old password is incorrect");
        }

        userRepository.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        logoutAll(user.getId());
        log.info("Password changed and tokens revoked for user {}", username);
    }

    // ---------------- PRIVATE ----------------

    private void handleFailedAttempt(UserDTO user) {
        int attempts = user.getFailedAttempts() + 1;

        if (attempts >= authProperties.getMaxFailedAttempts()) {
            var lockedUntil = LocalDateTime.now().plus(authProperties.getLockDurationMinutes(), ChronoUnit.MINUTES);
            userRepository.lockUser(user.getId(), lockedUntil);
            userRepository.resetFailedAttempts(user.getId());
            log.warn("User {} locked until {}", user.getUsername(), lockedUntil);
        } else {
            userRepository.increaseFailedAttempts(user.getId());
        }

        log.warn("Login failed for user {}, attempts={}", user.getUsername(), attempts);
    }

    private void resetLoginAttempts(UserDTO user) {
        userRepository.resetFailedAttempts(user.getId());
        userRepository.unlockUser(user.getId());
        userRepository.updateLastLogin(user.getId());
    }

    private AuthResponse generateTokens(UserDTO user) {
        logoutAll(user.getId());

        UserDTO userDetail = userRepository.findById(user.getId())
                .orElseThrow(() -> new AuthException("User detail not found"));

        String accessToken = jwtService.generateToken(
                user.getUsername(),
                userDetail.getRoles(),
                userDetail.getPermissions()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        userTokenRepository.insertToken(user.getId(), refreshToken);

        return AuthResponse.builder()
                .username(user.getUsername())
                .email(userDetail.getEmail())
                .avatar(userDetail.getAvatar())
                .provider("local")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(userDetail.getRoles())
                .permissions(userDetail.getPermissions())
                .build();
    }
}
