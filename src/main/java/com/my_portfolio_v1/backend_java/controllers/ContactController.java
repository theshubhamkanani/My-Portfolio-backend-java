package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ContactMessageAdminDTO;
import com.my_portfolio_v1.backend_java.dtos.ContactMessageDTO;
import com.my_portfolio_v1.backend_java.services.ContactMessageService;
import com.my_portfolio_v1.backend_java.services.RequestThrottleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final ContactMessageService contactMessageService;
    private final RequestThrottleService requestThrottleService;

    @PostMapping("/api/v1/public/contact")
    public ResponseEntity<String> submitContactForm(@Valid @RequestBody ContactMessageDTO dto,
                                                    HttpServletRequest request) {
        if (dto.getWebsite() != null && !dto.getWebsite().isBlank()) {
            return new ResponseEntity<>("Message received successfully.", HttpStatus.CREATED);
        }

        String clientIp = getClientIp(request);
        String normalizedEmail = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        requestThrottleService.assertAllowed("contact-ip:" + clientIp, 5, Duration.ofMinutes(15));
        requestThrottleService.assertAllowed("contact-email:" + normalizedEmail, 3, Duration.ofMinutes(30));

        contactMessageService.submitMessage(dto);
        return new ResponseEntity<>("Message received successfully.", HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/admin/contact")
    public ResponseEntity<List<ContactMessageAdminDTO>> getAllMessages(
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(contactMessageService.getAllMessages(search));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
