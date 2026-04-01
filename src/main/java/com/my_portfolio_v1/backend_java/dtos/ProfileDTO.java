package com.my_portfolio_v1.backend_java.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String fullName;
    private String profilePhotoUrl;
    private String titleLine;
    private String githubLink;
    private String linkedinLink;
    private String email;

    // We include simple lists of the related data
    private List<String> heroHeadlines;     // Filtered by type "HERO"
    private List<String> aboutDescriptions; // Filtered by type "ABOUT"
    private List<HighlightDTO> highlights;  // The bullet points
}