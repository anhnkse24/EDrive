package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ServiceCatalogRequest;
import com.swp391.edrive.dto.response.ServiceCatalogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdditionalServicesService {

    List<ServiceCatalogResponse> getAllActiveServices();

    ServiceCatalogResponse getServiceById(Long serviceId);

    List<ServiceCatalogResponse> getServicesByCategory(String category);

    // Tìm kiếm dịch vụ (với phân trang)
    Page<ServiceCatalogResponse> searchServices(String keyword, Pageable pageable);

    // Lấy tất cả dịch vụ (bao gồm inactive - cho admin)
    Page<ServiceCatalogResponse> getAllServices(Pageable pageable);

    ServiceCatalogResponse createService(ServiceCatalogRequest request);

    ServiceCatalogResponse updateService(Long serviceId, ServiceCatalogRequest request);

    // Xóa mềm dịch vụ (set isActive = false)
    void deactivateService(Long serviceId);

    void activateService(Long serviceId);

    // Xóa cứng dịch vụ
    void deleteService(Long serviceId);
}

