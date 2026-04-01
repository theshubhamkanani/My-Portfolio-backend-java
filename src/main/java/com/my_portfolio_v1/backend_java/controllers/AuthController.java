package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.AuthResponse;
import com.my_portfolio_v1.backend_java.dtos.LoginRequest;
import com.my_portfolio_v1.backend_java.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. If successful, generate the token
        String token = jwtUtil.generateToken(request.getEmail());

        // 3. Return the token to the frontend
        return ResponseEntity.ok(new AuthResponse(token));
    }
}