package com.example.jobportal.security;

import com.example.jobportal.data.entity.EmployerCompany;
import com.example.jobportal.repository.EmployerCompanyRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.EmployerCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmployerCompanyService employerCompanyService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Map roles + permissions
        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getPermissions().forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

        // Lấy danh sách companyId mà user thuộc về
        List<Long> companyIds = employerCompanyService
                .getCompaniesByEmployerId(user.getId())
                .stream()
                .map(EmployerCompany::getCompanyId)
                .collect(Collectors.toList());

        return new CustomUserDetails(
                user.getId(),
                companyIds,
                user.getUsername(),
                user.getPasswordHash(),
                true,
                authorities
        );
    }
}
