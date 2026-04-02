package com.my_portfolio_v1.backend_java.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageAdminDTO {
    private Long id;
    private String name;
    private String email;
    private String reason;
    private String description;
    private LocalDateTime createdAt;
}
