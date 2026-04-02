package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.SkillCategoryDTO;
import com.my_portfolio_v1.backend_java.dtos.SkillDTO;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.models.Skill;
import com.my_portfolio_v1.backend_java.models.SkillCategory;
import com.my_portfolio_v1.backend_java.repositories.SkillCategoryRepository;
import com.my_portfolio_v1.backend_java.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillCategoryService {

    private final SkillCategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final ProfileContextService profileContextService;

    @Transactional(readOnly = true)
    public List<SkillCategoryDTO> getAllSkillCategories(Long profileId) {
        Profile profile = profileContextService.resolveProfileForRead(profileId);
        if (profile == null) {
            return List.of();
        }

        return categoryRepository.findByProfileIdOrderByNameAsc(profile.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SkillDTO getSkillById(Long skillId) {
        Skill skill = findSkillById(skillId);
        return convertSkillToDTO(skill);
    }

    @Transactional
    public SkillCategoryDTO createSkillCategoryWithSkill(SkillCategoryDTO request) {
        Profile selectedProfile = profileContextService.resolveProfileForWrite(request.getProfileId());

        String categoryName = normalize(request.getName());
        if (categoryName == null) {
            throw new IllegalArgumentException("Category name is required.");
        }

        if (categoryRepository.findByProfileIdAndNameIgnoreCase(selectedProfile.getId(), categoryName).isPresent()) {
            throw new IllegalStateException("what dude, you have already this category, add your skill there.");
        }

        SkillDTO incomingSkill = extractFirstSkill(request);
        String skillName = normalize(incomingSkill.getName());
        if (skillName == null) {
            throw new IllegalArgumentException("Skill name is required.");
        }

        Integer level = incomingSkill.getLevel();
        if (level == null || level < 0 || level > 100) {
            throw new IllegalArgumentException("Skill level must be between 0 and 100.");
        }

        SkillCategory category = new SkillCategory();
        category.setName(categoryName);
        category.setProfile(selectedProfile);
        SkillCategory savedCategory = categoryRepository.save(category);

        Skill skill = new Skill();
        skill.setName(skillName);
        skill.setLevel(level);
        skill.setCategory(savedCategory);
        Skill savedSkill = skillRepository.save(skill);

        savedCategory.setSkills(List.of(savedSkill));
        return convertToDTO(savedCategory);
    }

    @Transactional
    public SkillCategoryDTO updateSkillCategory(Long categoryId, SkillCategoryDTO request) {
        SkillCategory category = findCategoryById(categoryId);

        String categoryName = normalize(request.getName());
        if (categoryName == null) {
            throw new IllegalArgumentException("Category name is required.");
        }

        Profile targetProfile = request.getProfileId() != null
                ? profileContextService.requireProfile(request.getProfileId())
                : category.getProfile() != null
                ? category.getProfile()
                : profileContextService.resolveProfileForWrite(null);

        if (categoryRepository.findByProfileIdAndNameIgnoreCaseAndIdNot(targetProfile.getId(), categoryName, categoryId).isPresent()) {
            throw new IllegalStateException("what dude, you have already this category, add your skill there.");
        }

        category.setName(categoryName);
        category.setProfile(targetProfile);

        SkillCategory updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Transactional
    public SkillDTO addSkillToCategory(Long categoryId, SkillDTO request) {
        SkillCategory category = findCategoryById(categoryId);

        String skillName = normalize(request.getName());
        if (skillName == null) {
            throw new IllegalArgumentException("Skill name is required.");
        }

        Integer level = request.getLevel();
        if (level == null || level < 0 || level > 100) {
            throw new IllegalArgumentException("Skill level must be between 0 and 100.");
        }

        Skill skill = new Skill();
        skill.setName(skillName);
        skill.setLevel(level);
        skill.setCategory(category);

        Skill savedSkill = skillRepository.save(skill);
        return convertSkillToDTO(savedSkill);
    }

    @Transactional
    public SkillDTO updateSkill(Long skillId, SkillDTO request) {
        Skill existingSkill = findSkillById(skillId);

        String skillName = normalize(request.getName());
        if (skillName == null) {
            throw new IllegalArgumentException("Skill name is required.");
        }

        Integer level = request.getLevel();
        if (level == null || level < 0 || level > 100) {
            throw new IllegalArgumentException("Skill level must be between 0 and 100.");
        }

        existingSkill.setName(skillName);
        existingSkill.setLevel(level);

        if (request.getCategoryId() != null) {
            SkillCategory category = findCategoryById(request.getCategoryId());
            existingSkill.setCategory(category);
        }

        Skill updatedSkill = skillRepository.save(existingSkill);
        return convertSkillToDTO(updatedSkill);
    }

    @Transactional
    public void deleteSkill(Long skillId) {
        Skill skill = findSkillById(skillId);
        skillRepository.delete(skill);
    }

    @Transactional
    public void deleteSkillCategory(Long categoryId) {
        SkillCategory category = findCategoryById(categoryId);

        if (category.getSkills() != null && !category.getSkills().isEmpty()) {
            throw new IllegalStateException("First remove all skills from this category.");
        }

        categoryRepository.delete(category);
    }

    private Skill findSkillById(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));
    }

    private SkillCategory findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Skill category not found with id: " + categoryId));
    }

    private SkillDTO extractFirstSkill(SkillCategoryDTO request) {
        if (request.getSkills() == null || request.getSkills().isEmpty()) {
            throw new IllegalArgumentException("Please add at least one skill.");
        }

        return request.getSkills().get(0);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }

    private SkillCategoryDTO convertToDTO(SkillCategory category) {
        SkillCategoryDTO dto = new SkillCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setProfileId(category.getProfile() != null ? category.getProfile().getId() : null);

        if (category.getSkills() != null) {
            List<SkillDTO> skillDTOs = category.getSkills().stream()
                    .map(this::convertSkillToDTO)
                    .collect(Collectors.toList());
            dto.setSkills(skillDTOs);
        }

        return dto;
    }

    private SkillDTO convertSkillToDTO(Skill skill) {
        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setLevel(skill.getLevel());

        if (skill.getCategory() != null) {
            dto.setCategoryId(skill.getCategory().getId());
            dto.setCategoryName(skill.getCategory().getName());
        }

        return dto;
    }
}
