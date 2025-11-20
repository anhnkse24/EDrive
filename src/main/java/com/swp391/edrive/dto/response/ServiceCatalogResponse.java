package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCatalogResponse {

    private Long serviceId;

    private String serviceName;

    private String description;

    private BigDecimal price;

    private Boolean isActive;

    private String category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

