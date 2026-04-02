package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileContextService {

    private final ProfileRepository profileRepository;

    @Value("${admin.personal.email:}")
    private String adminEmail;

    @Transactional(readOnly = true)
    public Profile getActiveProfileOrFirstOrNull() {
        return profileRepository.findFirstByLiveTrueOrderByIdAsc()
                .or(() -> profileRepository.findFirstByOrderByIdAsc())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Profile resolveProfileForRead(Long profileId) {
        if (profileId != null) {
            return requireProfile(profileId);
        }

        return getActiveProfileOrFirstOrNull();
    }

    @Transactional
    public Profile resolveProfileForWrite(Long profileId) {
        if (profileId != null) {
            return requireProfile(profileId);
        }

        return profileRepository.findFirstByLiveTrueOrderByIdAsc()
                .or(() -> profileRepository.findFirstByOrderByIdAsc())
                .orElseGet(this::createDefaultProfile);
    }

    @Transactional(readOnly = true)
    public Profile requireProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + profileId));
    }

    private Profile createDefaultProfile() {
        return profileRepository.save(
                Profile.builder()
                        .fullName("Portfolio Owner")
                        .email(adminEmail == null || adminEmail.isBlank() ? null : adminEmail)
                        .live(true)
                        .build()
        );
    }
}
