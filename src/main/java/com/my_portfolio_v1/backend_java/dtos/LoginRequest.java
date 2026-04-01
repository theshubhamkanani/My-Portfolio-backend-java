package com.my_portfolio_v1.backend_java.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
