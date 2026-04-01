package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "E-commerce API"

    private String designation; // e.g., "Backend Lead" or "Solo Developer"

    private String associatedWith; // e.g., "ThoughtFocus", "University Name", or "Personal"

    @Column(columnDefinition = "TEXT")
    private String description;

    // Optional Links
    private String githubLink;
    private String liveLink;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrentProject; // Our timeline flag! 🚩

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToMany
    @JoinTable(
            name = "project_skills",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills; // Placeholder for the Skills table
}