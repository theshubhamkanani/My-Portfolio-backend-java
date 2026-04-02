package com.my_portfolio_v1.backend_java.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HighlightDTO {
    private Long id;
    private String text;
    private Long profileId;
}
