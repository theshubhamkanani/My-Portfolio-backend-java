package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.EducationDTO;
import com.my_portfolio_v1.backend_java.models.Education;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.repositories.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final ProfileContextService profileContextService;

    @Transactional(readOnly = true)
    public List<EducationDTO> getPublicEducations() {
        Profile profile = profileContextService.getActiveProfileOrFirstOrNull();
        if (profile == null) {
            return List.of();
        }

        return educationRepository.findAllByProfileIdOrdered(profile.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EducationDTO> getAllEducations(Long profileId) {
        Profile profile = profileContextService.resolveProfileForRead(profileId);
        if (profile == null) {
            return List.of();
        }

        return educationRepository.findAllByProfileIdOrdered(profile.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EducationDTO getEducationById(Long id) {
        return convertToDTO(findEducationById(id));
    }

    @Transactional
    public EducationDTO saveEducation(EducationDTO dto) {
        Education education = new Education();
        applyDtoToEntity(education, dto);
        return convertToDTO(educationRepository.save(education));
    }

    @Transactional
    public EducationDTO updateEducation(Long id, EducationDTO dto) {
        Education existingEducation = findEducationById(id);
        applyDtoToEntity(existingEducation, dto);
        return convertToDTO(educationRepository.save(existingEducation));
    }

    @Transactional
    public void deleteEducation(Long id) {
        Education education = findEducationById(id);
        educationRepository.delete(education);
    }

    private Education findEducationById(Long id) {
        return educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + id));
    }

    private void applyDtoToEntity(Education education, EducationDTO dto) {
        Profile selectedProfile = profileContextService.resolveProfileForWrite(dto.getProfileId());

        education.setDegreeName(dto.getDegreeName());
        education.setInstituteName(dto.getInstituteName());
        education.setFromDate(dto.getFromDate());
        education.setToDate(dto.getToDate());
        education.setShortDescription(dto.getShortDescription());
        education.setProfile(selectedProfile);
    }

    private EducationDTO convertToDTO(Education education) {
        EducationDTO dto = new EducationDTO();
        dto.setId(education.getId());
        dto.setDegreeName(education.getDegreeName());
        dto.setInstituteName(education.getInstituteName());
        dto.setFromDate(education.getFromDate());
        dto.setToDate(education.getToDate());
        dto.setShortDescription(education.getShortDescription());
        dto.setProfileId(education.getProfile() != null ? education.getProfile().getId() : null);
        return dto;
    }
}
