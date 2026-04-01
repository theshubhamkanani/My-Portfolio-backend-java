package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String mobileNumber;

    @Column(nullable = false)
    private String password; // BCrypt hash, not plain text

    @Column(nullable = false)
    private String designation;
}