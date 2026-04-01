package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "descriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    // This is the missing field!
    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}