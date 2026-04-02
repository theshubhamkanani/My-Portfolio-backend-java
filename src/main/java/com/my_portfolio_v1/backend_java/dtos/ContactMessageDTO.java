package com.my_portfolio_v1.backend_java.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMessageDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name is too long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 160, message = "Email is too long")
    private String email;

    @NotBlank(message = "Reason is required")
    @Size(max = 120, message = "Reason is too long")
    private String reason;

    @NotBlank(message = "Message description cannot be empty")
    @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
    private String description;

    // Honeypot field: humans never fill this, simple bots often do.
    private String website;
}
