package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ExperienceDTO;
import com.my_portfolio_v1.backend_java.models.Experience;
import com.my_portfolio_v1.backend_java.services.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/experiences")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminExperienceController {

    private final ExperienceService experienceService;

    @GetMapping
    public ResponseEntity<List<ExperienceDTO>> getExperiences() {
        // Fetching experiences for the Super Admin (ID: 1)
        return ResponseEntity.ok(experienceService.getAllExperiencesFormatted(1L));
    }

    // Adds a new experience or updates an existing one
    @PostMapping("/add")
    public ResponseEntity<Experience> addExperience(@RequestBody Experience experience) {
        Experience savedExperience = experienceService.saveExperience(experience);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExperience);
    }

    // Deletes an experience by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.ok("Experience deleted successfully");
    }
}