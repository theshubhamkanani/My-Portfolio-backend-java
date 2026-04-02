package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.config.JwtUtil;
import com.my_portfolio_v1.backend_java.dtos.LoginRequest;
import com.my_portfolio_v1.backend_java.models.User;
import com.my_portfolio_v1.backend_java.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public String loginAdmin(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        String role = normalizeRole(user.getDesignation());

        if (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN")) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        return jwtUtil.generateToken(email);
    }

    public User registerAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDesignation("ADMIN");
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDesignation("USER");
        return userRepository.save(user);
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return "";
        }

        return rawRole.trim()
                .toUpperCase(Locale.ROOT)
                .replace("ROLE_", "")
                .replace(" ", "_")
                .replace("-", "_");
    }
}
