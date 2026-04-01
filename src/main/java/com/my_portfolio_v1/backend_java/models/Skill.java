package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "skills")
@Data
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer level; // Stores the percentage (e.g., 90)

    // Many skills belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private SkillCategory category;
}
