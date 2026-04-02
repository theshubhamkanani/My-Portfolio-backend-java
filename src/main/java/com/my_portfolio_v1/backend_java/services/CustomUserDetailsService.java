package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.models.User;
import com.my_portfolio_v1.backend_java.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(normalizeRole(user.getDesignation()))
                .build();
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            throw new UsernameNotFoundException("User role is missing");
        }

        return rawRole.trim()
                .toUpperCase()
                .replace("ROLE_", "")
                .replace(" ", "_")
                .replace("-", "_");
    }
}
