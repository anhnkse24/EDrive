package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ServiceCatalogRequest;
import com.swp391.edrive.dto.response.ServiceCatalogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdditionalServicesService {

    // Lấy tất cả dịch vụ đang hoạt động
    List<ServiceCatalogResponse> getAllActiveServices();

    // Lấy dịch vụ theo ID
    ServiceCatalogResponse getServiceById(Long serviceId);

    // Lấy dịch vụ theo category
    List<ServiceCatalogResponse> getServicesByCategory(String category);

    // Tìm kiếm dịch vụ (với phân trang)
    Page<ServiceCatalogResponse> searchServices(String keyword, Pageable pageable);

    // Lấy tất cả dịch vụ (bao gồm inactive - cho admin)
    Page<ServiceCatalogResponse> getAllServices(Pageable pageable);

    // Tạo dịch vụ mới
    ServiceCatalogResponse createService(ServiceCatalogRequest request);

    // Cập nhật dịch vụ
    ServiceCatalogResponse updateService(Long serviceId, ServiceCatalogRequest request);

    // Xóa mềm dịch vụ (set isActive = false)
    void deactivateService(Long serviceId);

    // Kích hoạt lại dịch vụ
    void activateService(Long serviceId);

    // Xóa cứng dịch vụ
    void deleteService(Long serviceId);
}

