package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/education")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminEducationController {

    private final EducationService educationService;

    @Autowired
    public AdminEducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    // Placeholder for adding new education records
    @PostMapping
    public ResponseEntity<String> addEducationRecord(@RequestBody Object newEducationData) {
        // Implementation for saving goes here
        return new ResponseEntity<>("Education record added successfully", HttpStatus.CREATED);
    }
}