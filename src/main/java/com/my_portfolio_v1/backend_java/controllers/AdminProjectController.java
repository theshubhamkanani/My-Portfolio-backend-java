package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.models.Project;
import com.my_portfolio_v1.backend_java.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProjectController {

    private final ProjectService projectService;

    // Adds a new project or updates an existing one
    @PostMapping("/add")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        Project savedProject = projectService.saveProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    // Deletes a project by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }
}