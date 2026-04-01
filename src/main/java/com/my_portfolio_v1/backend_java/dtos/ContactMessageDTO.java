package com.my_portfolio_v1.backend_java.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMessageDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Message description cannot be empty")
    @Size(min = 10, message = "Message must be at least 10 characters long")
    private String description;

}