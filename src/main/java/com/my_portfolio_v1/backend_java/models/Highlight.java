package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "highlights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Highlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text; // e.g., "Proven Track Record in Scalable Systems"

    private String iconName; // e.g., "check-circle"

    // Each highlight points back to the Profile it belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}