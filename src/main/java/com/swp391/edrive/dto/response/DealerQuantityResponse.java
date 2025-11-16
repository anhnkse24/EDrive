package com.swp391.edrive.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealerQuantityResponse {
    private String dealerName;
    private Integer quantity;
}