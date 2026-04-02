package com.my_portfolio_v1.backend_java.dtos;

import lombok.Data;

@Data
public class SkillDTO {
    private Long id;
    private String name;
    private Integer level;
    private Long categoryId;
    private String categoryName;
}
