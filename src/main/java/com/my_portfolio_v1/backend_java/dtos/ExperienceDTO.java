package com.my_portfolio_v1.backend_java.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    private String startDate;
    private String endDate;
    private boolean isCurrentJob;
    private Long profileId;

    @JsonAlias("technologies")
    private List<String> skills;
}
