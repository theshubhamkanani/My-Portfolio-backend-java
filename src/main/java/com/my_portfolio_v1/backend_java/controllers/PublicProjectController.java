package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ProjectDTO;
import com.my_portfolio_v1.backend_java.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PublicProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getProjects() {
        // Fetching projects for the Super Admin (ID: 1L)
        return ResponseEntity.ok(projectService.getAllProjectsFormatted(1L));
    }
}