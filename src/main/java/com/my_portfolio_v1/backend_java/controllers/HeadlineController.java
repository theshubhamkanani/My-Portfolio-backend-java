package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.HeadlineDTO;
import com.my_portfolio_v1.backend_java.services.HeadlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HeadlineController {

    private final HeadlineService headlineService;

    @GetMapping("/api/v1/admin/headlines")
    public ResponseEntity<List<HeadlineDTO>> getHeadlines(@RequestParam(required = false) Long profileId) {
        return ResponseEntity.ok(headlineService.getAllHeadlines(profileId));
    }

    @PostMapping("/api/v1/admin/headlines")
    public ResponseEntity<HeadlineDTO> addHeadline(@RequestBody HeadlineDTO headlineDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(headlineService.saveHeadline(headlineDTO));
    }

    @PutMapping("/api/v1/admin/headlines/{id}")
    public ResponseEntity<HeadlineDTO> updateHeadline(@PathVariable Long id, @RequestBody HeadlineDTO headlineDTO) {
        return ResponseEntity.ok(headlineService.updateHeadline(id, headlineDTO));
    }

    @PatchMapping("/api/v1/admin/headlines/{id}/live")
    public ResponseEntity<HeadlineDTO> activateHeadline(@PathVariable Long id) {
        return ResponseEntity.ok(headlineService.activateHeadline(id));
    }

    @DeleteMapping("/api/v1/admin/headlines/{id}")
    public ResponseEntity<String> deleteHeadline(@PathVariable Long id) {
        headlineService.deleteHeadline(id);
        return ResponseEntity.ok("Headline deleted successfully");
    }
}
