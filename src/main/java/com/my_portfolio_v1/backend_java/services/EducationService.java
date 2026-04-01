package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.EducationDTO;
import com.my_portfolio_v1.backend_java.models.Education;
import com.my_portfolio_v1.backend_java.repositories.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {

    private final EducationRepository educationRepository;

    @Autowired
    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    @Transactional(readOnly = true)
    public List<EducationDTO> getAllEducations() {
        List<Education> educations = educationRepository.findAll();
        return educations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EducationDTO convertToDTO(Education education) {
        EducationDTO dto = new EducationDTO();
        dto.setId(education.getId());
        dto.setDegreeName(education.getDegreeName());
        dto.setInstituteName(education.getInstituteName());
        dto.setFromDate(education.getFromDate());
        dto.setToDate(education.getToDate());
        dto.setShortDescription(education.getShortDescription());
        return dto;
    }
}