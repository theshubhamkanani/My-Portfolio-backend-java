package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.ProjectDTO;
import com.my_portfolio_v1.backend_java.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/api/v1/public/projects")
    public ResponseEntity<List<ProjectDTO>> getProjects() {
        return ResponseEntity.ok(projectService.getAllProjectsFormatted());
    }

    @GetMapping("/api/v1/admin/projects")
    public ResponseEntity<List<ProjectDTO>> getAdminProjects(
            @RequestParam(required = false) Long profileId
    ) {
        return ResponseEntity.ok(projectService.getAllProjectsForAdmin(profileId));
    }

    @GetMapping("/api/v1/admin/projects/{id}")
    public ResponseEntity<ProjectDTO> getAdminProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectForAdmin(id));
    }

    @PostMapping("/api/v1/admin/projects")
    public ResponseEntity<ProjectDTO> addProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.saveProject(projectDTO));
    }

    @PutMapping("/api/v1/admin/projects/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }

    @DeleteMapping("/api/v1/admin/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }
}
