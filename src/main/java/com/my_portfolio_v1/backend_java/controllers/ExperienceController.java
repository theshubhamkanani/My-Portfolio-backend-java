package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ExperienceDTO;
import com.my_portfolio_v1.backend_java.services.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping("/api/v1/public/experiences")
    public ResponseEntity<List<ExperienceDTO>> getExperiences() {
        return ResponseEntity.ok(experienceService.getAllExperiencesFormatted());
    }

    @GetMapping("/api/v1/admin/experiences")
    public ResponseEntity<List<ExperienceDTO>> getAdminExperiences(
            @RequestParam(required = false) Long profileId
    ) {
        return ResponseEntity.ok(experienceService.getAllExperiencesForAdmin(profileId));
    }

    @GetMapping("/api/v1/admin/experiences/{id}")
    public ResponseEntity<ExperienceDTO> getAdminExperienceById(@PathVariable Long id) {
        return ResponseEntity.ok(experienceService.getExperienceForAdmin(id));
    }

    @PostMapping("/api/v1/admin/experiences")
    public ResponseEntity<ExperienceDTO> addExperience(@RequestBody ExperienceDTO experienceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(experienceService.saveExperience(experienceDTO));
    }

    @PutMapping("/api/v1/admin/experiences/{id}")
    public ResponseEntity<ExperienceDTO> updateExperience(
            @PathVariable Long id,
            @RequestBody ExperienceDTO experienceDTO
    ) {
        return ResponseEntity.ok(experienceService.updateExperience(id, experienceDTO));
    }

    @DeleteMapping("/api/v1/admin/experiences/{id}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.ok("Experience deleted successfully");
    }
}
