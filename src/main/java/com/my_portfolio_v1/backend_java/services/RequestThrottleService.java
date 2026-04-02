package com.my_portfolio_v1.backend_java.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RequestThrottleService {

    private final ConcurrentHashMap<String, Deque<Long>> requestStore = new ConcurrentHashMap<>();

    public void assertAllowed(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        long windowMs = window.toMillis();

        Deque<Long> timestamps = requestStore.computeIfAbsent(key, unused -> new ArrayDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= windowMs) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= maxRequests) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too many requests. Please try again later."
                );
            }

            timestamps.addLast(now);
        }
    }
}
