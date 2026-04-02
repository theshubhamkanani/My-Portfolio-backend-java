package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.DescriptionDTO;
import com.my_portfolio_v1.backend_java.services.DescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DescriptionController {

    private final DescriptionService descriptionService;

    @GetMapping("/api/v1/admin/descriptions")
    public ResponseEntity<List<DescriptionDTO>> getDescriptions(@RequestParam(required = false) Long profileId) {
        return ResponseEntity.ok(descriptionService.getAllDescriptions(profileId));
    }

    @PostMapping("/api/v1/admin/descriptions")
    public ResponseEntity<DescriptionDTO> addDescription(@RequestBody DescriptionDTO descriptionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(descriptionService.saveDescription(descriptionDTO));
    }

    @PutMapping("/api/v1/admin/descriptions/{id}")
    public ResponseEntity<DescriptionDTO> updateDescription(@PathVariable Long id,
                                                            @RequestBody DescriptionDTO descriptionDTO) {
        return ResponseEntity.ok(descriptionService.updateDescription(id, descriptionDTO));
    }

    @PatchMapping("/api/v1/admin/descriptions/{id}/live")
    public ResponseEntity<DescriptionDTO> activateDescription(@PathVariable Long id) {
        return ResponseEntity.ok(descriptionService.activateDescription(id));
    }

    @DeleteMapping("/api/v1/admin/descriptions/{id}")
    public ResponseEntity<String> deleteDescription(@PathVariable Long id) {
        descriptionService.deleteDescription(id);
        return ResponseEntity.ok("Description deleted successfully");
    }
}
