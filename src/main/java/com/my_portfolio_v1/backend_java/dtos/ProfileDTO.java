package com.my_portfolio_v1.backend_java.dtos;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String profilePhotoUrl;
    private String titleLine;
    private String githubLink;
    private String linkedinLink;
    private String email;
    private boolean live;

    private String heroHeadline;
    private String heroDescription;
    private String aboutHeadline;
    private String aboutDescription;

    private List<HighlightDTO> highlights;
}
