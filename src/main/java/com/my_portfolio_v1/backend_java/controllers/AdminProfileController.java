package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProfileController {

    private final ProfileService profileService;

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Profile profile) {
        // We will implement the update logic in the service next
        return ResponseEntity.ok("Profile updated successfully");
    }
}