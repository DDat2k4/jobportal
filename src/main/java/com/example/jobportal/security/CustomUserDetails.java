package com.example.jobportal.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final List<Long> companyIds;
    private final String username;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, List<Long> companyIds, String username, String password, boolean active,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.companyIds = companyIds;
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}

