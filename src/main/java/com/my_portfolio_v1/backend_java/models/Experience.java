package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String designation;
    private String companyName;
    private String companyLogoUrl;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrentJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToMany
    @JoinTable(
            name = "experience_skills",
            joinColumns = @JoinColumn(name = "experience_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills; // To be linked to the Skills table later
}