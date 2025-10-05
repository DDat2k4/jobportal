package com.example.jobportal.service;

import com.example.jobportal.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var record = authRepository.findUserWithRolesAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add roles as ROLE_xxx
        record.roles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
        );

        // Add permissions as plain authorities
        record.perms().forEach(perm ->
                authorities.add(new SimpleGrantedAuthority(perm))
        );

        log.debug("Loaded user: {} with roles={} and perms={}", username, record.roles(), record.perms());

        return new org.springframework.security.core.userdetails.User(
                record.username(),
                record.password(),
                true,  // enabled
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}
