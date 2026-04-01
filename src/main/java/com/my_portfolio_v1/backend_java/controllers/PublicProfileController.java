package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ProfileDTO;
import com.my_portfolio_v1.backend_java.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PublicProfileController {

    private final ProfileService profileService;

    // We fetch ID 1 since this is a personal portfolio with one main owner
    @GetMapping("/summary")
    public ResponseEntity<ProfileDTO> getProfileSummary() {
        return ResponseEntity.ok(profileService.getCompleteProfile(1L));
    }
}