package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.AdminLoginResponseDTO;
import com.my_portfolio_v1.backend_java.dtos.LoginRequest;
import com.my_portfolio_v1.backend_java.services.AdminTabSessionService;
import com.my_portfolio_v1.backend_java.services.AuthService;
import com.my_portfolio_v1.backend_java.services.RequestThrottleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RequestThrottleService requestThrottleService;
    private final AdminTabSessionService adminTabSessionService;

    @Value("${app.security.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${app.security.cookie-same-site:Lax}")
    private String cookieSameSite;

    @Value("${app.security.cookie-domain:}")
    private String cookieDomain;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDTO> login(@Valid @RequestBody LoginRequest request,
                                                       HttpServletRequest httpRequest,
                                                       HttpServletResponse response) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String clientIp = getClientIp(httpRequest);

        requestThrottleService.assertAllowed("login-ip:" + clientIp, 10, Duration.ofMinutes(15));
        requestThrottleService.assertAllowed("login-email:" + normalizedEmail, 10, Duration.ofMinutes(15));

        try {
            String jwtToken = authService.loginAdmin(request);
            String tabToken = adminTabSessionService.issueTabToken(normalizedEmail, Duration.ofHours(1));

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    buildAuthCookie(jwtToken, Duration.ofHours(1)).toString()
            );

            return ResponseEntity.ok(new AdminLoginResponseDTO("Login successful.", tabToken));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AdminLoginResponseDTO("Invalid credentials.", null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String tabToken = request.getHeader("X-Admin-Tab-Token");

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            adminTabSessionService.clear(authentication.getName(), tabToken);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, buildClearedAuthCookie().toString());
        return ResponseEntity.ok("Logout successful.");
    }

    @GetMapping("/session")
    public ResponseEntity<Void> getSession(Authentication authentication) {
        if (isAdminAuthenticated(authentication)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean isAdminAuthenticated(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.getAuthorities().stream().anyMatch(authority ->
                "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_SUPER_ADMIN".equals(authority.getAuthority())
        );
    }

    private ResponseCookie buildAuthCookie(String token, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("portfolio_token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(maxAge);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain.trim());
        }

        return builder.build();
    }

    private ResponseCookie buildClearedAuthCookie() {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("portfolio_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ZERO);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain.trim());
        }

        return builder.build();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
