package com.my_portfolio_v1.backend_java.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "educations")
@Data
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "degree_name", nullable = false)
    private String degreeName;

    @Column(name = "institute_name", nullable = false)
    private String instituteName;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    // This field is nullable. A null value here will represent "Present"
    @Column(name = "to_date")
    private LocalDate toDate;

    // Using TEXT for the description allows for longer paragraphs if needed
    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

}