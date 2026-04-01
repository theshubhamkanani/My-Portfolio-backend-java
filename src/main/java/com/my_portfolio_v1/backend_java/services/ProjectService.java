package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ProjectDTO;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.models.Project;
import com.my_portfolio_v1.backend_java.repositories.ProfileRepository;
import com.my_portfolio_v1.backend_java.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProfileRepository profileRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    // READ: Get all projects formatted for the frontend
    public List<ProjectDTO> getAllProjectsFormatted(Long profileId) {
        List<Project> projects = projectRepository.findAllByProfileIdSorted(profileId);

        return projects.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // CREATE/UPDATE: Save a project attached to Super Admin
    @Transactional
    public Project saveProject(Project project) {
        Profile superAdmin = profileRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Super Admin Profile not initialized!"));

        project.setProfile(superAdmin);

        if (project.isCurrentProject()) {
            project.setEndDate(null);
        }

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    // --- HELPER METHOD ---
    private ProjectDTO mapToDTO(Project proj) {
        String formattedStartDate = proj.getStartDate() != null ? proj.getStartDate().format(formatter) : "";

        String formattedEndDate = "Present";
        if (!proj.isCurrentProject() && proj.getEndDate() != null) {
            formattedEndDate = proj.getEndDate().format(formatter);
        }

        return ProjectDTO.builder()
                .id(proj.getId())
                .title(proj.getTitle())
                .designation(proj.getDesignation())
                .associatedWith(proj.getAssociatedWith())
                .description(proj.getDescription())
                .githubLink(proj.getGithubLink())
                .liveLink(proj.getLiveLink())
                .startDate(formattedStartDate)
                .endDate(formattedEndDate)
                .isCurrentProject(proj.isCurrentProject())
                .technologies(List.of()) // Ready for the Skills table!
                .build();
    }
}