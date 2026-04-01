package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ExperienceDTO;
import com.my_portfolio_v1.backend_java.models.Experience;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.ExperienceRepository;
import com.my_portfolio_v1.backend_java.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ProfileRepository profileRepository;

    // A formatter to get "Month Year" (e.g., "April 2025")
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    // READ: Get all experiences formatted for the frontend
    public List<ExperienceDTO> getAllExperiencesFormatted(Long profileId) {
        List<Experience> experiences = experienceRepository.findAllByProfileIdSorted(profileId);

        return experiences.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // CREATE/UPDATE: Save an experience attached to Super Admin
    @Transactional
    public Experience saveExperience(Experience experience) {
        Profile superAdmin = profileRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Super Admin Profile not initialized!"));

        experience.setProfile(superAdmin);

        if (experience.isCurrentJob()) {
            experience.setEndDate(null);
        }

        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(Long id) {
        experienceRepository.deleteById(id);
    }

    // --- HELPER METHOD ---

    private ExperienceDTO mapToDTO(Experience exp) {
        String formattedStartDate = exp.getStartDate() != null ? exp.getStartDate().format(formatter) : "";

        // Our "Present" logic in action!
        String formattedEndDate = "Present";
        if (!exp.isCurrentJob() && exp.getEndDate() != null) {
            formattedEndDate = exp.getEndDate().format(formatter);
        }

        return ExperienceDTO.builder()
                .id(exp.getId())
                .designation(exp.getDesignation())
                .companyName(exp.getCompanyName())
                .companyLogoUrl(exp.getCompanyLogoUrl())
                .location(exp.getLocation())
                .description(exp.getDescription())
                .startDate(formattedStartDate)
                .endDate(formattedEndDate)
                .isCurrentJob(exp.isCurrentJob())
                .technologies(List.of()) // We will populate this later when Skills are built!
                .build();
    }
}