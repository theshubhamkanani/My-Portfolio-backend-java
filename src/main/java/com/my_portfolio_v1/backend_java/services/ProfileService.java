package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.HighlightDTO;
import com.my_portfolio_v1.backend_java.dtos.ProfileDTO;
import com.my_portfolio_v1.backend_java.models.Description;
import com.my_portfolio_v1.backend_java.models.Headline;
import com.my_portfolio_v1.backend_java.models.Highlight;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileContextService profileContextService;
    private final ProfileImageStorageService profileImageStorageService;


    @Transactional(readOnly = true)
    public ProfileDTO getCompleteProfile() {
        Profile profile = profileContextService.getActiveProfileOrFirstOrNull();

        if (profile == null) {
            return emptyProfile();
        }

        return mapToDTO(profile);
    }

    @Transactional
    public ProfileDTO getAdminProfile(Long profileId) {
        Profile profile = profileId != null
                ? profileContextService.requireProfile(profileId)
                : profileContextService.resolveProfileForWrite(null);

        return mapToDTO(profile);
    }

    @Transactional
    public List<ProfileDTO> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAllByOrderByIdAsc();

        if (profiles.isEmpty()) {
            return List.of(mapToDTO(profileContextService.resolveProfileForWrite(null)));
        }

        return profiles.stream()
                .sorted(Comparator.comparing(Profile::isLive).reversed().thenComparing(Profile::getId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfileDTO createProfile(ProfileDTO dto) {
        Profile profile = new Profile();
        applyProfileFields(profile, dto);
        profile.setLive(profileRepository.count() == 0);
        return mapToDTO(profileRepository.save(profile));
    }

    @Transactional
    public ProfileDTO updateProfile(ProfileDTO dto) {
        Profile profile = dto.getId() != null
                ? profileContextService.requireProfile(dto.getId())
                : profileContextService.resolveProfileForWrite(null);

        boolean currentLiveState = profile.isLive();
        applyProfileFields(profile, dto);
        profile.setLive(currentLiveState);

        return mapToDTO(profileRepository.save(profile));
    }

    @Transactional
    public ProfileDTO updateProfile(Long id, ProfileDTO dto) {
        Profile profile = profileContextService.requireProfile(id);

        boolean currentLiveState = profile.isLive();
        applyProfileFields(profile, dto);
        profile.setLive(currentLiveState);

        return mapToDTO(profileRepository.save(profile));
    }

    @Transactional
    public ProfileDTO activateProfile(Long id) {
        profileContextService.requireProfile(id);

        List<Profile> profiles = profileRepository.findAllByOrderByIdAsc();
        for (Profile profile : profiles) {
            profile.setLive(Objects.equals(profile.getId(), id));
        }

        profileRepository.saveAll(profiles);

        return mapToDTO(profileContextService.requireProfile(id));
    }

    private void applyProfileFields(Profile profile, ProfileDTO dto) {
        if (!Objects.equals(profile.getProfilePhotoUrl(), dto.getProfilePhotoUrl())) {
            profileImageStorageService.deleteManagedProfileImage(profile.getProfilePhotoUrl());
        }

        profile.setFullName(dto.getFullName());
        profile.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        profile.setTitleLine(dto.getTitleLine());
        profile.setGithubLink(dto.getGithubLink());
        profile.setLinkedinLink(dto.getLinkedinLink());
        profile.setEmail(dto.getEmail());
    }

    private ProfileDTO emptyProfile() {
        return ProfileDTO.builder()
                .live(false)
                .heroHeadline(null)
                .heroDescription(null)
                .aboutHeadline(null)
                .aboutDescription(null)
                .highlights(List.of())
                .build();
    }

    private ProfileDTO mapToDTO(Profile profile) {
        return ProfileDTO.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .titleLine(profile.getTitleLine())
                .githubLink(profile.getGithubLink())
                .linkedinLink(profile.getLinkedinLink())
                .email(profile.getEmail())
                .live(profile.isLive())
                .heroHeadline(resolveHeadlineText(profile.getHeadlines(), "HERO"))
                .heroDescription(resolveDescriptionText(profile.getDescriptions(), "HERO"))
                .aboutHeadline(resolveHeadlineText(profile.getHeadlines(), "ABOUT"))
                .aboutDescription(resolveDescriptionText(profile.getDescriptions(), "ABOUT"))
                .highlights(mapHighlights(profile.getHighlights(), profile.getId()))
                .build();
    }

    private String resolveHeadlineText(List<Headline> headlines, String type) {
        if (headlines == null) {
            return null;
        }

        return headlines.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getType() != null && item.getType().equalsIgnoreCase(type))
                .sorted(Comparator.comparing(Headline::isLive).reversed().thenComparing(Headline::getId))
                .map(Headline::getText)
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String resolveDescriptionText(List<Description> descriptions, String type) {
        if (descriptions == null) {
            return null;
        }

        return descriptions.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getType() != null && item.getType().equalsIgnoreCase(type))
                .sorted(Comparator.comparing(Description::isLive).reversed().thenComparing(Description::getId))
                .map(Description::getText)
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElse(null);
    }


    private List<HighlightDTO> mapHighlights(List<Highlight> highlights, Long profileId) {
        if (highlights == null) {
            return List.of();
        }

        return highlights.stream()
                .filter(Objects::nonNull)
                .map(h -> HighlightDTO.builder()
                        .id(h.getId())
                        .text(h.getText())
                        .profileId(profileId)
                        .build())
                .collect(Collectors.toList());
    }
}
