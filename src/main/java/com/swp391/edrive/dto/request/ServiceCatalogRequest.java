package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceCatalogRequest {

    private String serviceName;

    private String description;

    private BigDecimal price;

    private Boolean isActive;

    private String category;
}

