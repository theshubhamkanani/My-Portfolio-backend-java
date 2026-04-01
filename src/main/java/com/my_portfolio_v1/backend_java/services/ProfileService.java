package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.*;
import com.my_portfolio_v1.backend_java.models.*;
import com.my_portfolio_v1.backend_java.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final HeadlineRepository headlineRepository;
    private final DescriptionRepository descriptionRepository;
    private final HighlightRepository highlightRepository;

    public ProfileDTO getCompleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return ProfileDTO.builder()
                .fullName(profile.getFullName())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .titleLine(profile.getTitleLine())
                .githubLink(profile.getGithubLink())
                .linkedinLink(profile.getLinkedinLink())
                .email(profile.getEmail())
                .heroHeadlines(filterHeadlines(profile.getHeadlines(), "HERO"))
                .aboutDescriptions(filterDescriptions(profile.getDescriptions(), "ABOUT"))
                .highlights(mapHighlights(profile.getHighlights()))
                .build();
    }

    @Transactional
    public Profile updateProfile(Profile updatedProfile) {
        // 1. Verify existence (Production rule: never trust client IDs blindly)
        Profile existingProfile = profileRepository.findById(updatedProfile.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 2. Update core fields
        existingProfile.setFullName(updatedProfile.getFullName());
        existingProfile.setProfilePhotoUrl(updatedProfile.getProfilePhotoUrl());
        existingProfile.setTitleLine(updatedProfile.getTitleLine());
        existingProfile.setGithubLink(updatedProfile.getGithubLink());
        existingProfile.setLinkedinLink(updatedProfile.getLinkedinLink());
        existingProfile.setEmail(updatedProfile.getEmail());

        // 3. Handle Child Entities (Linking them to the parent profile)
        if (updatedProfile.getHeadlines() != null) {
            updatedProfile.getHeadlines().forEach(h -> h.setProfile(existingProfile));
        }
        if (updatedProfile.getDescriptions() != null) {
            updatedProfile.getDescriptions().forEach(d -> d.setProfile(existingProfile));
        }
        if (updatedProfile.getHighlights() != null) {
            updatedProfile.getHighlights().forEach(hi -> hi.setProfile(existingProfile));
        }

        return profileRepository.save(existingProfile);
    }

    // --- HELPER METHODS ---

    private List<String> filterHeadlines(List<Headline> headlines, String type) {
        if (headlines == null) return List.of();
        return headlines.stream()
                .filter(h -> h.getType() != null && h.getType().equalsIgnoreCase(type))
                .map(Headline::getText)
                .collect(Collectors.toList());
    }

    private List<String> filterDescriptions(List<Description> descriptions, String type) {
        if (descriptions == null) return List.of();
        return descriptions.stream()
                .filter(d -> d.getType() != null && d.getType().equalsIgnoreCase(type))
                .map(Description::getText)
                .collect(Collectors.toList());
    }

    private List<HighlightDTO> mapHighlights(List<Highlight> highlights) {
        if (highlights == null) return List.of();
        return highlights.stream()
                .map(h -> {
                    HighlightDTO dto = new HighlightDTO();
                    dto.setText(h.getText());
                    dto.setIconName(h.getIconName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}