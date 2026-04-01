package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.SkillCategoryDTO;
import com.my_portfolio_v1.backend_java.services.SkillCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/skills")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicSkillController {

    private final SkillCategoryService skillCategoryService;

    @Autowired
    public PublicSkillController(SkillCategoryService skillCategoryService) {
        this.skillCategoryService = skillCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<SkillCategoryDTO>> getAllSkillsForPortfolio() {
        List<SkillCategoryDTO> skills = skillCategoryService.getAllSkillCategories();
        return ResponseEntity.ok(skills);
    }
}