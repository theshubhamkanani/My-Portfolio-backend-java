package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.config.JwtUtil;
import com.my_portfolio_v1.backend_java.dtos.AuthResponse;
import com.my_portfolio_v1.backend_java.dtos.LoginRequest;
import com.my_portfolio_v1.backend_java.models.User;
import com.my_portfolio_v1.backend_java.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        // This triggers the CustomUserDetailsService we wrote earlier
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If no exception was thrown, the user is valid
        String token = jwtUtil.generateToken(request.getEmail());
        return new AuthResponse(token);
    }

    public User registerAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Match the "ROLE_ADMIN" naming convention used in SecurityConfig
        user.setDesignation("ADMIN");
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDesignation("USER");
        return userRepository.save(user);
    }
}