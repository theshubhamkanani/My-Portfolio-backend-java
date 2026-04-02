package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.EducationDTO;
import com.my_portfolio_v1.backend_java.services.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/api/v1/public/education")
    public ResponseEntity<List<EducationDTO>> getEducationHistory() {
        return ResponseEntity.ok(educationService.getPublicEducations());
    }

    @GetMapping("/api/v1/admin/education")
    public ResponseEntity<List<EducationDTO>> getAdminEducationHistory(
            @RequestParam(required = false) Long profileId
    ) {
        return ResponseEntity.ok(educationService.getAllEducations(profileId));
    }

    @GetMapping("/api/v1/admin/education/{id}")
    public ResponseEntity<EducationDTO> getEducationById(@PathVariable Long id) {
        return ResponseEntity.ok(educationService.getEducationById(id));
    }

    @PostMapping("/api/v1/admin/education")
    public ResponseEntity<EducationDTO> addEducation(@RequestBody EducationDTO educationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.saveEducation(educationDTO));
    }

    @PutMapping("/api/v1/admin/education/{id}")
    public ResponseEntity<EducationDTO> updateEducation(
            @PathVariable Long id,
            @RequestBody EducationDTO educationDTO
    ) {
        return ResponseEntity.ok(educationService.updateEducation(id, educationDTO));
    }

    @DeleteMapping("/api/v1/admin/education/{id}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return ResponseEntity.ok("Education deleted successfully");
    }
}
