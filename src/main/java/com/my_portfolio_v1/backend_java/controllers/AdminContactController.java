package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.models.ContactMessage;
import com.my_portfolio_v1.backend_java.services.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminContactController {

    private final ContactMessageService contactMessageService;

    @Autowired
    public AdminContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        List<ContactMessage> messages = contactMessageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
}