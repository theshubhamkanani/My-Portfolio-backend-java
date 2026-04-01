package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String profilePhotoUrl;
    private String titleLine; // e.g., "Software Engineer & Java Specialist"

    // Social & Contact
    private String githubLink;
    private String linkedinLink;
    private String email;

    // One Profile can have many Headlines
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Headline> headlines;

    // One Profile can have many Descriptions
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Description> descriptions;

    // One Profile can have many Highlights (the bullet points with checkmarks)
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Highlight> highlights;
}