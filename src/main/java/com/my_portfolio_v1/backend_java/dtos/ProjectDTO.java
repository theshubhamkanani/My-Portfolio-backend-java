package com.my_portfolio_v1.backend_java.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String projectName;
    private String organizationName;
    private String designation;
    private String description;
    private String githubLink;
    private String liveLink;
    private String startDate;
    private String endDate;
    private boolean isCurrentProject;
    private Long profileId;

    @JsonAlias("technologies")
    private List<String> skills;
}
