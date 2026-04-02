package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.HighlightDTO;
import com.my_portfolio_v1.backend_java.services.HighlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HighlightController {

    private final HighlightService highlightService;

    @GetMapping("/api/v1/admin/highlights")
    public ResponseEntity<List<HighlightDTO>> getHighlights(@RequestParam(required = false) Long profileId) {
        return ResponseEntity.ok(highlightService.getAllHighlights(profileId));
    }

    @PostMapping("/api/v1/admin/highlights")
    public ResponseEntity<HighlightDTO> addHighlight(@RequestBody HighlightDTO highlightDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(highlightService.saveHighlight(highlightDTO));
    }

    @PutMapping("/api/v1/admin/highlights/{id}")
    public ResponseEntity<HighlightDTO> updateHighlight(@PathVariable Long id,
                                                        @RequestBody HighlightDTO highlightDTO) {
        return ResponseEntity.ok(highlightService.updateHighlight(id, highlightDTO));
    }

    @DeleteMapping("/api/v1/admin/highlights/{id}")
    public ResponseEntity<String> deleteHighlight(@PathVariable Long id) {
        highlightService.deleteHighlight(id);
        return ResponseEntity.ok("Highlight deleted successfully");
    }
}
