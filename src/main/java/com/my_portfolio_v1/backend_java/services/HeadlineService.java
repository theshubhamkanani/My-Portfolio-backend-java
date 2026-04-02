package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.HeadlineDTO;
import com.my_portfolio_v1.backend_java.models.Headline;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.HeadlineRepository;
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
public class HeadlineService {

    private static final Set<String> ALLOWED_TYPES = Set.of("HERO", "ABOUT");

    private final HeadlineRepository headlineRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public List<HeadlineDTO> getAllHeadlines(Long profileId) {
        Profile profile = resolveProfile(profileId);

        return headlineRepository.findByProfileIdOrderByIdAsc(profile.getId())
                .stream()
                .sorted(Comparator.comparing(Headline::isLive).reversed().thenComparing(Headline::getId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HeadlineDTO saveHeadline(HeadlineDTO dto) {
        Profile profile = resolveProfile(dto.getProfileId());
        String type = normalizeType(dto.getType());

        boolean shouldMakeLive = Boolean.TRUE.equals(dto.getLive())
                || !hasLiveHeadline(profile.getId(), type);

        if (shouldMakeLive) {
            deactivateOtherHeadlines(profile.getId(), type, null);
        }

        Headline headline = Headline.builder()
                .text(normalizeText(dto.getText()))
                .type(type)
                .live(shouldMakeLive)
                .profile(profile)
                .build();

        return mapToDTO(headlineRepository.save(headline));
    }

    @Transactional
    public HeadlineDTO updateHeadline(Long id, HeadlineDTO dto) {
        Headline headline = headlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Headline not found with id: " + id));

        Long previousProfileId = headline.getProfile() != null ? headline.getProfile().getId() : null;
        String previousType = headline.getType();
        boolean wasLive = headline.isLive();

        Profile profile = resolveProfile(dto.getProfileId());
        String type = normalizeType(dto.getType());
        boolean shouldMakeLive = dto.getLive() != null ? dto.getLive() : wasLive;

        if (shouldMakeLive) {
            deactivateOtherHeadlines(profile.getId(), type, id);
        }

        headline.setText(normalizeText(dto.getText()));
        headline.setType(type);
        headline.setLive(shouldMakeLive);
        headline.setProfile(profile);

        Headline savedHeadline = headlineRepository.save(headline);

        if (wasLive && (!sameType(previousType, type) || !previousProfileId.equals(profile.getId()))) {
            promoteFirstHeadlineIfNeeded(previousProfileId, previousType);
        }

        return mapToDTO(savedHeadline);
    }

    @Transactional
    public HeadlineDTO activateHeadline(Long id) {
        Headline headline = headlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Headline not found with id: " + id));

        Long profileId = headline.getProfile() != null ? headline.getProfile().getId() : null;
        if (profileId == null) {
            throw new RuntimeException("Headline is not linked to a profile.");
        }

        String type = normalizeType(headline.getType());
        deactivateOtherHeadlines(profileId, type, id);

        headline.setLive(true);
        return mapToDTO(headlineRepository.save(headline));
    }

    @Transactional
    public void deleteHeadline(Long id) {
        Headline headline = headlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Headline not found with id: " + id));

        Long profileId = headline.getProfile() != null ? headline.getProfile().getId() : null;
        String type = headline.getType();
        boolean wasLive = headline.isLive();

        headlineRepository.delete(headline);

        if (wasLive) {
            promoteFirstHeadlineIfNeeded(profileId, type);
        }
    }

    private void deactivateOtherHeadlines(Long profileId, String type, Long activeId) {
        List<Headline> sameTypeHeadlines = headlineRepository.findByProfileIdOrderByIdAsc(profileId)
                .stream()
                .filter(item -> sameType(item.getType(), type))
                .collect(Collectors.toList());

        boolean changed = false;

        for (Headline item : sameTypeHeadlines) {
            if (activeId != null && activeId.equals(item.getId())) {
                continue;
            }

            if (item.isLive()) {
                item.setLive(false);
                changed = true;
            }
        }

        if (changed) {
            headlineRepository.saveAll(sameTypeHeadlines);
        }
    }

    private void promoteFirstHeadlineIfNeeded(Long profileId, String type) {
        if (profileId == null || type == null || type.isBlank()) {
            return;
        }

        List<Headline> sameTypeHeadlines = headlineRepository.findByProfileIdOrderByIdAsc(profileId)
                .stream()
                .filter(item -> sameType(item.getType(), type))
                .collect(Collectors.toList());

        boolean hasLive = sameTypeHeadlines.stream().anyMatch(Headline::isLive);

        if (!hasLive && !sameTypeHeadlines.isEmpty()) {
            Headline firstHeadline = sameTypeHeadlines.get(0);
            firstHeadline.setLive(true);
            headlineRepository.save(firstHeadline);
        }
    }

    private boolean hasLiveHeadline(Long profileId, String type) {
        return headlineRepository.findByProfileIdOrderByIdAsc(profileId)
                .stream()
                .anyMatch(item -> sameType(item.getType(), type) && item.isLive());
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Headline text is required.");
        }

        return value.trim();
    }

    private String normalizeType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Headline type is required.");
        }

        String normalizedType = value.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException("Headline type must be HERO or ABOUT.");
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

    private HeadlineDTO mapToDTO(Headline headline) {
        return HeadlineDTO.builder()
                .id(headline.getId())
                .text(headline.getText())
                .type(headline.getType())
                .live(headline.isLive())
                .profileId(headline.getProfile() != null ? headline.getProfile().getId() : null)
                .build();
    }
}
