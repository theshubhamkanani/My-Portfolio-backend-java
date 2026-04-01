package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String reason;

    // Using TEXT because user messages can be quite long
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // This automatically sets the timestamp right before the entity is saved to the DB
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}