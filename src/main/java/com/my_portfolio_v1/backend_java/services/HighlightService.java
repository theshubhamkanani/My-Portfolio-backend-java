package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.HighlightDTO;
import com.my_portfolio_v1.backend_java.models.Highlight;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.HighlightRepository;
import com.my_portfolio_v1.backend_java.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public List<HighlightDTO> getAllHighlights(Long profileId) {
        Profile profile = resolveProfile(profileId);

        return highlightRepository.findByProfileIdOrderByIdAsc(profile.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HighlightDTO saveHighlight(HighlightDTO dto) {
        Highlight highlight = new Highlight();
        applyDtoToEntity(highlight, dto);
        return mapToDTO(highlightRepository.save(highlight));
    }

    @Transactional
    public HighlightDTO updateHighlight(Long id, HighlightDTO dto) {
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight not found with id: " + id));

        applyDtoToEntity(highlight, dto);
        return mapToDTO(highlightRepository.save(highlight));
    }

    @Transactional
    public void deleteHighlight(Long id) {
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight not found with id: " + id));

        highlightRepository.delete(highlight);
    }

    private void applyDtoToEntity(Highlight highlight, HighlightDTO dto) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new IllegalArgumentException("Highlight text is required.");
        }

        highlight.setText(dto.getText().trim());
        highlight.setProfile(resolveProfile(dto.getProfileId()));
    }

    private Profile resolveProfile(Long profileId) {
        if (profileId != null) {
            return profileRepository.findById(profileId)
                    .orElseThrow(() -> new RuntimeException("Profile not found with id: " + profileId));
        }

        return profileRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> profileRepository.save(Profile.builder().build()));
    }

    private HighlightDTO mapToDTO(Highlight highlight) {
        return HighlightDTO.builder()
                .id(highlight.getId())
                .text(highlight.getText())
                .profileId(highlight.getProfile() != null ? highlight.getProfile().getId() : null)
                .build();
    }
}
