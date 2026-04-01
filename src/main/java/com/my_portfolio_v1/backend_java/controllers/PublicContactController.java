package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ContactMessageDTO;
import com.my_portfolio_v1.backend_java.services.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicContactController {

    private final ContactMessageService contactMessageService;

    @Autowired
    public PublicContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    public ResponseEntity<String> submitContactForm(@Valid @RequestBody ContactMessageDTO dto) {
        // 1. Save to database immediately
        contactMessageService.processAndSaveMessage(dto);

        // 2. Trigger the emails to run in the background
        contactMessageService.sendNotificationEmails(dto);

        // 3. Return a successful response to the React frontend without waiting for SMTP
        return new ResponseEntity<>("Message saved and emails triggered.", HttpStatus.CREATED);
    }
}