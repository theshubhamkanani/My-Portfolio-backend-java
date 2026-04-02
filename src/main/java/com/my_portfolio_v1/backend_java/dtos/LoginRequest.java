package com.my_portfolio_v1.backend_java.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 160, message = "Email is too long")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 200, message = "Password is too long")
    private String password;
}
