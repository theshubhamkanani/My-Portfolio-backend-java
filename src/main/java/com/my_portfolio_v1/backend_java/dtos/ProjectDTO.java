package com.my_portfolio_v1.backend_java.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String title;
    private String designation;
    private String associatedWith;
    private String description;

    // Links
    private String githubLink;
    private String liveLink;

    // Formatted dates (e.g., "June 2024")
    private String startDate;
    private String endDate;

    private boolean isCurrentProject;

    // Placeholder for the frontend tags
    private List<String> technologies;
}