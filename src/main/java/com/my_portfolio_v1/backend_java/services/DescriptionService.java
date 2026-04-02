package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.DescriptionDTO;
import com.my_portfolio_v1.backend_java.models.Description;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.DescriptionRepository;
import com.my_portfolio_v1.backend_java.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DescriptionService {

    private static final Set<String> ALLOWED_TYPES = Set.of("HERO", "ABOUT");

    private final DescriptionRepository descriptionRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public List<DescriptionDTO> getAllDescriptions(Long profileId) {
        Profile profile = resolveProfile(profileId);

        return descriptionRepository.findByProfileIdOrderByIdAsc(profile.getId())
                .stream()
                .sorted(Comparator.comparing(Description::isLive).reversed().thenComparing(Description::getId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DescriptionDTO saveDescription(DescriptionDTO dto) {
        Profile profile = resolveProfile(dto.getProfileId());
        String type = normalizeType(dto.getType());

        boolean shouldMakeLive = Boolean.TRUE.equals(dto.getLive())
                || !hasLiveDescription(profile.getId(), type);

        if (shouldMakeLive) {
            deactivateOtherDescriptions(profile.getId(), type, null);
        }

        Description description = Description.builder()
                .text(normalizeText(dto.getText()))
                .type(type)
                .live(shouldMakeLive)
                .profile(profile)
                .build();

        return mapToDTO(descriptionRepository.save(description));
    }

    @Transactional
    public DescriptionDTO updateDescription(Long id, DescriptionDTO dto) {
        Description description = descriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Description not found with id: " + id));

        Long previousProfileId = description.getProfile() != null ? description.getProfile().getId() : null;
        String previousType = description.getType();
        boolean wasLive = description.isLive();

        Profile profile = resolveProfile(dto.getProfileId());
        String type = normalizeType(dto.getType());
        boolean shouldMakeLive = dto.getLive() != null ? dto.getLive() : wasLive;

        if (shouldMakeLive) {
            deactivateOtherDescriptions(profile.getId(), type, id);
        }

        description.setText(normalizeText(dto.getText()));
        description.setType(type);
        description.setLive(shouldMakeLive);
        description.setProfile(profile);

        Description savedDescription = descriptionRepository.save(description);

        if (wasLive && (!sameType(previousType, type) || !previousProfileId.equals(profile.getId()))) {
            promoteFirstDescriptionIfNeeded(previousProfileId, previousType);
        }

        return mapToDTO(savedDescription);
    }

    @Transactional
    public DescriptionDTO activateDescription(Long id) {
        Description description = descriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Description not found with id: " + id));

        Long profileId = description.getProfile() != null ? description.getProfile().getId() : null;
        if (profileId == null) {
            throw new RuntimeException("Description is not linked to a profile.");
        }

        String type = normalizeType(description.getType());
        deactivateOtherDescriptions(profileId, type, id);

        description.setLive(true);
        return mapToDTO(descriptionRepository.save(description));
    }

    @Transactional
    public void deleteDescription(Long id) {
        Description description = descriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Description not found with id: " + id));

        Long profileId = description.getProfile() != null ? description.getProfile().getId() : null;
        String type = description.getType();
        boolean wasLive = description.isLive();

        descriptionRepository.delete(description);

        if (wasLive) {
            promoteFirstDescriptionIfNeeded(profileId, type);
        }
    }

    private void deactivateOtherDescriptions(Long profileId, String type, Long activeId) {
        List<Description> sameTypeDescriptions = descriptionRepository.findByProfileIdOrderByIdAsc(profileId)
                .stream()
                .filter(item -> sameType(item.getType(), type))
                .collect(Collectors.toList());

        boolean changed = false;

        for (Description item : sameTypeDescriptions) {
            if (activeId != null && activeId.equals(item.getId())) {
                continue;
            }

            if (item.isLive()) {
                item.setLive(false);
                changed = true;
            }
        }

        if (changed) {
            descriptionRepository.saveAll(sameTypeDescriptions);
        }
    }

    private void promoteFirstDescriptionIfNeeded(Long profileId, String type) {
        if (profileId == null || type == null || type.isBlank()) {
            return;
        }

        List<Description> sameTypeDescriptions = descriptionRepository.findByProfileIdOrderByIdAsc(profileId)
                .stream()
                .filter(item -> sameType(item.getType(), type))
                .collect(Collectors.toList());

        boolean hasLive = sameTypeDescriptions.stream().anyMatch(Description::isLive);

        if (!hasLive && !sameTypeDescriptions.isEmpty()) {
            Description firstDescription = sameTypeDescriptions.get(0);
            firstDescription.setLive(true);
            descriptionRepository.save(firstDescription);
        }
    }

    private boolean hasLiveDescription(Long profileId, String type) {
        return descriptionRepository.findByProfileIdOrderByIdAsc(profileId)
                .stream()
                .anyMatch(item -> sameType(item.getType(), type) && item.isLive());
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Description text is required.");
        }

        return value.trim();
    }

    private String normalizeType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Description type is required.");
        }

        String normalizedType = value.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException("Description type must be HERO or ABOUT.");
        }

        return normalizedType;
    }

    private boolean sameType(String left, String right) {
        return left != null && right != null && left.equalsIgnoreCase(right);
    }

    private Profile resolveProfile(Long profileId) {
        if (profileId != null) {
            return profileRepository.findById(profileId)
                    .orElseThrow(() -> new RuntimeException("Profile not found with id: " + profileId));
        }

        return profileRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> profileRepository.save(Profile.builder().build()));
    }

    private DescriptionDTO mapToDTO(Description description) {
        return DescriptionDTO.builder()
                .id(description.getId())
                .text(description.getText())
                .type(description.getType())
                .live(description.isLive())
                .profileId(description.getProfile() != null ? description.getProfile().getId() : null)
                .build();
    }
}
