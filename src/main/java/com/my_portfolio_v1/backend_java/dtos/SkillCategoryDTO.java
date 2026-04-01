package com.my_portfolio_v1.backend_java.dtos;

import lombok.Data;

import java.util.List;
@Data
public class SkillCategoryDTO {
    private Long id;
    private String name;
    private List<SkillDTO> skills;


}
