package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ProfileDTO;
import com.my_portfolio_v1.backend_java.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/api/v1/public/profile/summary")
    public ResponseEntity<ProfileDTO> getProfileSummary() {
        return ResponseEntity.ok(profileService.getCompleteProfile());
    }

    @GetMapping("/api/v1/admin/profile")
    public ResponseEntity<ProfileDTO> getAdminProfile(@RequestParam(required = false) Long profileId) {
        return ResponseEntity.ok(profileService.getAdminProfile(profileId));
    }

    @GetMapping("/api/v1/admin/profiles")
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @PostMapping("/api/v1/admin/profiles")
    public ResponseEntity<ProfileDTO> createProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfile(profileDTO));
    }

    @PutMapping("/api/v1/admin/profile")
    public ResponseEntity<ProfileDTO> updateProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(profileService.updateProfile(profileDTO));
    }

    @PutMapping("/api/v1/admin/profiles/{id}")
    public ResponseEntity<ProfileDTO> updateProfileById(@PathVariable Long id, @RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(profileService.updateProfile(id, profileDTO));
    }

    @PatchMapping("/api/v1/admin/profiles/{id}/live")
    public ResponseEntity<ProfileDTO> activateProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.activateProfile(id));
    }
}
