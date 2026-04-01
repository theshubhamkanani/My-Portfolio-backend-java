package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.SkillCategoryDTO;
import com.my_portfolio_v1.backend_java.dtos.SkillDTO;
import com.my_portfolio_v1.backend_java.models.Skill;
import com.my_portfolio_v1.backend_java.models.SkillCategory;
import com.my_portfolio_v1.backend_java.repositories.SkillCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillCategoryService {

    private final SkillCategoryRepository categoryRepository;

    @Autowired
    public SkillCategoryService(SkillCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillCategoryDTO> getAllSkillCategories() {
        // Fetch all categories (which automatically fetches their skills due to our JPA setup)
        List<SkillCategory> categories = categoryRepository.findAll();

        // Transform the Entities into DTOs
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Category Entity -> Category DTO
    private SkillCategoryDTO convertToDTO(SkillCategory category) {
        SkillCategoryDTO dto = new SkillCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        // Safely map the nested list of skills
        if (category.getSkills() != null) {
            List<SkillDTO> skillDTOs = category.getSkills().stream()
                    .map(this::convertSkillToDTO)
                    .collect(Collectors.toList());
            dto.setSkills(skillDTOs);
        }
        return dto;
    }

    // Helper method to convert Skill Entity -> Skill DTO
    private SkillDTO convertSkillToDTO(Skill skill) {
        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setLevel(skill.getLevel());
        return dto;
    }
}
