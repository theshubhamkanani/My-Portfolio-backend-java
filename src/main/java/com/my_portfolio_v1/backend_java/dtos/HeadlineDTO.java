package com.my_portfolio_v1.backend_java.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeadlineDTO {
    private Long id;
    private String text;
    private String type;
    private Boolean live;
    private Long profileId;
}
