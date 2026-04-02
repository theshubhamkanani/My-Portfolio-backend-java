package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ProjectDTO;
import com.my_portfolio_v1.backend_java.models.Profile;
import com.my_portfolio_v1.backend_java.models.Project;
import com.my_portfolio_v1.backend_java.models.Skill;
import com.my_portfolio_v1.backend_java.repositories.ProjectRepository;
import com.my_portfolio_v1.backend_java.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ProfileContextService profileContextService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjectsFormatted() {
        Profile profile = profileContextService.getActiveProfileOrFirstOrNull();
        if (profile == null) {
            return List.of();
        }

        return projectRepository.findAllByProfileIdSorted(profile.getId())
                .stream()
                .map(this::mapToPublicDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjectsForAdmin(Long profileId) {
        Profile profile = profileContextService.resolveProfileForRead(profileId);
        if (profile == null) {
            return List.of();
        }

        return projectRepository.findAllByProfileIdSorted(profile.getId())
                .stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDTO getProjectForAdmin(Long id) {
        return mapToAdminDTO(findProjectById(id));
    }

    @Transactional
    public ProjectDTO saveProject(ProjectDTO dto) {
        Project project = new Project();
        applyDtoToProject(project, dto);
        return mapToAdminDTO(projectRepository.save(project));
    }

    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project existingProject = findProjectById(id);
        applyDtoToProject(existingProject, dto);
        return mapToAdminDTO(projectRepository.save(existingProject));
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }

    private void applyDtoToProject(Project project, ProjectDTO dto) {
        Profile selectedProfile = profileContextService.resolveProfileForWrite(dto.getProfileId());

        project.setTitle(trimToNull(dto.getProjectName()));
        project.setAssociatedWith(trimToNull(dto.getOrganizationName()));
        project.setDesignation(trimToNull(dto.getDesignation()));
        project.setDescription(trimToNull(dto.getDescription()));
        project.setGithubLink(trimToNull(dto.getGithubLink()));
        project.setLiveLink(trimToNull(dto.getLiveLink()));
        project.setStartDate(parseDate(dto.getStartDate()));
        project.setCurrentProject(dto.isCurrentProject());
        project.setEndDate(dto.isCurrentProject() ? null : parseDate(dto.getEndDate()));
        project.setProfile(selectedProfile);
        project.setSkills(resolveSkills(selectedProfile.getId(), dto.getSkills()));
    }

    private Set<Skill> resolveSkills(Long profileId, List<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<String> cleanedSkillNames = skillNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (cleanedSkillNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Skill> skills = skillRepository.findByCategoryProfileIdAndNameIn(profileId, cleanedSkillNames);

        if (skills.size() != cleanedSkillNames.size()) {
            Set<String> foundNames = skills.stream()
                    .map(Skill::getName)
                    .collect(Collectors.toSet());

            List<String> missingSkills = cleanedSkillNames.stream()
                    .filter(name -> !foundNames.contains(name))
                    .collect(Collectors.toList());

            throw new RuntimeException("These skills were not found for the selected profile: " + String.join(", ", missingSkills));
        }

        return new LinkedHashSet<>(skills);
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ProjectDTO mapToAdminDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .projectName(project.getTitle())
                .organizationName(project.getAssociatedWith())
                .designation(project.getDesignation())
                .description(project.getDescription())
                .githubLink(project.getGithubLink())
                .liveLink(project.getLiveLink())
                .startDate(project.getStartDate() != null ? project.getStartDate().toString() : null)
                .endDate(project.getEndDate() != null ? project.getEndDate().toString() : null)
                .isCurrentProject(project.isCurrentProject())
                .profileId(project.getProfile() != null ? project.getProfile().getId() : null)
                .skills(extractSkillNames(project))
                .build();
    }

    private ProjectDTO mapToPublicDTO(Project project) {
        String formattedStartDate = project.getStartDate() != null
                ? project.getStartDate().format(formatter)
                : "";

        String formattedEndDate = "Present";
        if (!project.isCurrentProject() && project.getEndDate() != null) {
            formattedEndDate = project.getEndDate().format(formatter);
        }

        return ProjectDTO.builder()
                .id(project.getId())
                .projectName(project.getTitle())
                .organizationName(project.getAssociatedWith())
                .designation(project.getDesignation())
                .description(project.getDescription())
                .githubLink(project.getGithubLink())
                .liveLink(project.getLiveLink())
                .startDate(formattedStartDate)
                .endDate(formattedEndDate)
                .isCurrentProject(project.isCurrentProject())
                .profileId(project.getProfile() != null ? project.getProfile().getId() : null)
                .skills(extractSkillNames(project))
                .build();
    }

    private List<String> extractSkillNames(Project project) {
        if (project.getSkills() == null || project.getSkills().isEmpty()) {
            return List.of();
        }

        return project.getSkills().stream()
                .filter(Objects::nonNull)
                .map(Skill::getName)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }
}
