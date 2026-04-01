package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.SkillCategoryDTO;
// Assuming you will create these DTOs for data entry:
// import com.my_portfolio_v1.backend_java.dtos.CreateSkillCategoryDTO;
import com.my_portfolio_v1.backend_java.services.SkillCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/skills")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminSkillController {

    private final SkillCategoryService skillCategoryService;

    @Autowired
    public AdminSkillController(SkillCategoryService skillCategoryService) {
        this.skillCategoryService = skillCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<SkillCategoryDTO>> getAllSkillsForPortfolio() {
        List<SkillCategoryDTO> skills = skillCategoryService.getAllSkillCategories();
        return ResponseEntity.ok(skills);
    }

    // Example placeholder for the POST mapping you will build in the Service layer next
    @PostMapping("/category")
    public ResponseEntity<String> createSkillCategory(@RequestBody Object newCategoryData) {
        // skillCategoryService.createCategory(newCategoryData);
        return new ResponseEntity<>("Category created successfully", HttpStatus.CREATED);
    }

    // Additional PUT and DELETE mappings would go here
}
