package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectedServiceResponse {
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
}

