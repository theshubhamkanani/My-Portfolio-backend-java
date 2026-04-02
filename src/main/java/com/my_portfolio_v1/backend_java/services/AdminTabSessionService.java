package com.my_portfolio_v1.backend_java.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminTabSessionService {

    private final ConcurrentHashMap<String, TabSession> activeTabSessions = new ConcurrentHashMap<>();

    public String issueTabToken(String email, Duration ttl) {
        String normalizedEmail = normalizeEmail(email);
        String tabToken = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + ttl.toMillis();

        activeTabSessions.put(normalizedEmail, new TabSession(tabToken, expiresAt));
        return tabToken;
    }

    public boolean isValid(String email, String tabToken) {
        if (email == null || email.isBlank() || tabToken == null || tabToken.isBlank()) {
            return false;
        }

        String normalizedEmail = normalizeEmail(email);
        TabSession session = activeTabSessions.get(normalizedEmail);

        if (session == null) {
            return false;
        }

        if (session.expiresAt < System.currentTimeMillis()) {
            activeTabSessions.remove(normalizedEmail);
            return false;
        }

        return session.tabToken.equals(tabToken);
    }

    public void clear(String email, String tabToken) {
        if (email == null || email.isBlank() || tabToken == null || tabToken.isBlank()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);

        activeTabSessions.computeIfPresent(
                normalizedEmail,
                (key, session) -> session.tabToken.equals(tabToken) ? null : session
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static final class TabSession {
        private final String tabToken;
        private final long expiresAt;

        private TabSession(String tabToken, long expiresAt) {
            this.tabToken = tabToken;
            this.expiresAt = expiresAt;
        }
    }
}
