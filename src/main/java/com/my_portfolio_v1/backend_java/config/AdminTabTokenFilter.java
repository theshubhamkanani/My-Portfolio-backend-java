package com.my_portfolio_v1.backend_java.config;

import com.my_portfolio_v1.backend_java.services.AdminTabSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AdminTabTokenFilter extends OncePerRequestFilter {

    private final AdminTabSessionService adminTabSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!requiresTabValidation(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        String tabToken = request.getHeader("X-Admin-Tab-Token");
        String email = authentication.getName();

        if (!adminTabSessionService.isValid(email, tabToken)) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Tab authorization required.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresTabValidation(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/api/v1/admin/")
                || "/api/v1/auth/session".equals(uri);
    }
}
