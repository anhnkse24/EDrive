package com.swp391.edrive.mapper;

import com.swp391.edrive.dto.request.ServiceCatalogRequest;
import com.swp391.edrive.dto.response.ServiceCatalogResponse;
import com.swp391.edrive.entity.AdditionalServices;
import org.springframework.stereotype.Component;

@Component
public class AdditionalServicesMapper {

    public ServiceCatalogResponse toResponse(AdditionalServices service) {
        return ServiceCatalogResponse.builder()
                .serviceId(service.getServiceId())
                .serviceName(service.getServiceName())
                .description(service.getDescription())
                .price(service.getPrice())
                .isActive(service.getIsActive())
                .category(service.getCategory())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }

    public AdditionalServices toEntity(ServiceCatalogRequest request) {
        AdditionalServices service = new AdditionalServices();
        service.setServiceName(request.getServiceName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        service.setCategory(request.getCategory());
        return service;
    }

    public void updateEntity(AdditionalServices service, ServiceCatalogRequest request) {
        if (request.getServiceName() != null) {
            service.setServiceName(request.getServiceName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            service.setPrice(request.getPrice());
        }
        if (request.getIsActive() != null) {
            service.setIsActive(request.getIsActive());
        }
        if (request.getCategory() != null) {
            service.setCategory(request.getCategory());
        }
    }
}

