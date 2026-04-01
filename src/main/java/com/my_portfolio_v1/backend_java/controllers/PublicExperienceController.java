package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ExperienceDTO;
import com.my_portfolio_v1.backend_java.services.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/experiences")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PublicExperienceController {

    private final ExperienceService experienceService;

    @GetMapping
    public ResponseEntity<List<ExperienceDTO>> getExperiences() {
        // Fetching experiences for the Super Admin (ID: 1)
        return ResponseEntity.ok(experienceService.getAllExperiencesFormatted(1L));
    }
}