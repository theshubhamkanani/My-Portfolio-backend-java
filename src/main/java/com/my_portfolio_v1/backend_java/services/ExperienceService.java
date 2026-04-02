package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ExperienceDTO;
import com.my_portfolio_v1.backend_java.models.Experience;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.models.Skill;
import com.my_portfolio_v1.backend_java.repositories.ExperienceRepository;
import com.my_portfolio_v1.backend_java.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final ProfileContextService profileContextService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    @Transactional(readOnly = true)
    public List<ExperienceDTO> getAllExperiencesFormatted() {
        Profile profile = profileContextService.getActiveProfileOrFirstOrNull();
        if (profile == null) {
            return List.of();
        }

        return experienceRepository.findAllByProfileIdSorted(profile.getId())
                .stream()
                .map(this::mapToPublicDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExperienceDTO> getAllExperiencesForAdmin(Long profileId) {
        Profile profile = profileContextService.resolveProfileForRead(profileId);
        if (profile == null) {
            return List.of();
        }

        return experienceRepository.findAllByProfileIdSorted(profile.getId())
                .stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExperienceDTO getExperienceForAdmin(Long id) {
        return mapToAdminDTO(findExperienceById(id));
    }

    @Transactional
    public ExperienceDTO saveExperience(ExperienceDTO dto) {
        Experience experience = new Experience();
        applyDtoToExperience(experience, dto);
        return mapToAdminDTO(experienceRepository.save(experience));
    }

    @Transactional
    public ExperienceDTO updateExperience(Long id, ExperienceDTO dto) {
        Experience existingExperience = findExperienceById(id);
        applyDtoToExperience(existingExperience, dto);
        return mapToAdminDTO(experienceRepository.save(existingExperience));
    }

    @Transactional
    public void deleteExperience(Long id) {
        Experience experience = findExperienceById(id);
        experienceRepository.delete(experience);
    }

    private void applyDtoToExperience(Experience experience, ExperienceDTO dto) {
        Profile selectedProfile = profileContextService.resolveProfileForWrite(dto.getProfileId());

        experience.setDesignation(dto.getDesignation());
        experience.setCompanyName(dto.getCompanyName());
        experience.setCompanyLogoUrl(dto.getCompanyLogoUrl());
        experience.setLocation(dto.getLocation());
        experience.setDescription(dto.getDescription());
        experience.setStartDate(parseDate(dto.getStartDate()));
        experience.setCurrentJob(dto.isCurrentJob());
        experience.setEndDate(dto.isCurrentJob() ? null : parseDate(dto.getEndDate()));
        experience.setProfile(selectedProfile);
        experience.setSkills(resolveSkills(selectedProfile.getId(), dto.getSkills()));
    }

    private Set<Skill> resolveSkills(Long profileId, List<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<String> cleanedSkillNames = skillNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (cleanedSkillNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Skill> skills = skillRepository.findByCategoryProfileIdAndNameIn(profileId, cleanedSkillNames);

        if (skills.size() != cleanedSkillNames.size()) {
            Set<String> foundNames = skills.stream()
                    .map(Skill::getName)
                    .collect(Collectors.toSet());

            List<String> missingSkills = cleanedSkillNames.stream()
                    .filter(name -> !foundNames.contains(name))
                    .collect(Collectors.toList());

            throw new RuntimeException("These skills were not found for the selected profile: " + String.join(", ", missingSkills));
        }

        return new LinkedHashSet<>(skills);
    }

    private Experience findExperienceById(Long id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + id));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private ExperienceDTO mapToAdminDTO(Experience experience) {
        return ExperienceDTO.builder()
                .id(experience.getId())
                .designation(experience.getDesignation())
                .companyName(experience.getCompanyName())
                .companyLogoUrl(experience.getCompanyLogoUrl())
                .location(experience.getLocation())
                .description(experience.getDescription())
                .startDate(experience.getStartDate() != null ? experience.getStartDate().toString() : null)
                .endDate(experience.getEndDate() != null ? experience.getEndDate().toString() : null)
                .isCurrentJob(experience.isCurrentJob())
                .profileId(experience.getProfile() != null ? experience.getProfile().getId() : null)
                .skills(extractSkillNames(experience))
                .build();
    }

    private ExperienceDTO mapToPublicDTO(Experience experience) {
        String formattedStartDate = experience.getStartDate() != null
                ? experience.getStartDate().format(formatter)
                : "";

        String formattedEndDate = "Present";
        if (!experience.isCurrentJob() && experience.getEndDate() != null) {
            formattedEndDate = experience.getEndDate().format(formatter);
        }

        return ExperienceDTO.builder()
                .id(experience.getId())
                .designation(experience.getDesignation())
                .companyName(experience.getCompanyName())
                .companyLogoUrl(experience.getCompanyLogoUrl())
                .location(experience.getLocation())
                .description(experience.getDescription())
                .startDate(formattedStartDate)
                .endDate(formattedEndDate)
                .isCurrentJob(experience.isCurrentJob())
                .profileId(experience.getProfile() != null ? experience.getProfile().getId() : null)
                .skills(extractSkillNames(experience))
                .build();
    }

    private List<String> extractSkillNames(Experience experience) {
        if (experience.getSkills() == null || experience.getSkills().isEmpty()) {
            return List.of();
        }

        return experience.getSkills().stream()
                .filter(Objects::nonNull)
                .map(Skill::getName)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }
}
