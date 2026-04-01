package com.my_portfolio_v1.backend_java.dtos;

import lombok.Data;

import java.time.LocalDate;
@Data
public class EducationDTO {

    private Long id;
    private String degreeName;
    private String instituteName;
    private LocalDate fromDate;
    private LocalDate toDate; // Null indicates "Present"
    private String shortDescription;

}