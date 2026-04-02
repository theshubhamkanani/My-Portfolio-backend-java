package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.SkillCategoryDTO;
import com.my_portfolio_v1.backend_java.dtos.SkillDTO;
import com.my_portfolio_v1.backend_java.services.SkillCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillCategoryService skillCategoryService;

    @GetMapping("/api/v1/public/skills")
    public ResponseEntity<List<SkillCategoryDTO>> getAllSkills() {
        return ResponseEntity.ok(skillCategoryService.getAllSkillCategories(null));
    }

    @GetMapping("/api/v1/admin/skills")
    public ResponseEntity<List<SkillCategoryDTO>> getAllAdminSkills(
            @RequestParam(required = false) Long profileId
    ) {
        return ResponseEntity.ok(skillCategoryService.getAllSkillCategories(profileId));
    }

    @GetMapping("/api/v1/admin/skills/{id}")
    public ResponseEntity<?> getSkillById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(skillCategoryService.getSkillById(id));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/v1/admin/skills/category")
    public ResponseEntity<?> createSkillCategory(@RequestBody SkillCategoryDTO newCategoryData) {
        try {
            SkillCategoryDTO createdData = skillCategoryService.createSkillCategoryWithSkill(newCategoryData);
            return new ResponseEntity<>(createdData, HttpStatus.CREATED);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/v1/admin/skills/category/{id}")
    public ResponseEntity<?> updateSkillCategory(@PathVariable Long id, @RequestBody SkillCategoryDTO categoryData) {
        try {
            return ResponseEntity.ok(skillCategoryService.updateSkillCategory(id, categoryData));
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/v1/admin/skills/category/{id}/skill")
    public ResponseEntity<?> addSkillToCategory(@PathVariable Long id, @RequestBody SkillDTO skillData) {
        try {
            return new ResponseEntity<>(skillCategoryService.addSkillToCategory(id, skillData), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/api/v1/admin/skills/{id}")
    public ResponseEntity<?> updateSkill(@PathVariable Long id, @RequestBody SkillDTO skillData) {
        try {
            SkillDTO updatedSkill = skillCategoryService.updateSkill(id, skillData);
            return ResponseEntity.ok(updatedSkill);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/v1/admin/skills/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        try {
            skillCategoryService.deleteSkill(id);
            return ResponseEntity.ok("Skill deleted successfully.");
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/v1/admin/skills/category/{id}")
    public ResponseEntity<?> deleteSkillCategory(@PathVariable Long id) {
        try {
            skillCategoryService.deleteSkillCategory(id);
            return ResponseEntity.ok("Skill category deleted successfully.");
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
