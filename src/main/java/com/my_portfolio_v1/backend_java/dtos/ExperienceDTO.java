package com.my_portfolio_v1.backend_java.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private Long id;
    private String designation;
    private String companyName;
    private String companyLogoUrl;
    private String location;
    private String description;

    // Formatted dates (e.g., "April 2025")
    private String startDate;
    private String endDate;   // Will be "Present" if isCurrentJob is true

    private boolean isCurrentJob;

    // A simple list of skill names for the frontend tags
    private List<String> technologies;
}