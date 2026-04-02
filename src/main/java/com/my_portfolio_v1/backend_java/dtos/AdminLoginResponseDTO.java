package com.my_portfolio_v1.backend_java.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponseDTO {
    private String message;
    private String tabToken;
}
