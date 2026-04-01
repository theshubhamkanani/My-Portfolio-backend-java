package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.EducationDTO;
import com.my_portfolio_v1.backend_java.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/education")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicEducationController {

    private final EducationService educationService;

    @Autowired
    public PublicEducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public ResponseEntity<List<EducationDTO>> getEducationHistory() {
        List<EducationDTO> educationList = educationService.getAllEducations();
        return ResponseEntity.ok(educationList);
    }
}