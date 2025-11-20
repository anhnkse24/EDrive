package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ServiceCatalogRequest;
import com.swp391.edrive.dto.response.ServiceCatalogResponse;
import com.swp391.edrive.entity.AdditionalServices;
import com.swp391.edrive.mapper.AdditionalServicesMapper;
import com.swp391.edrive.repository.AdditionalServicesRepository;
import com.swp391.edrive.service.AdditionalServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdditionalServicesServiceImpl implements AdditionalServicesService {

    private final AdditionalServicesRepository servicesRepository;
    private final AdditionalServicesMapper servicesMapper;

    @Override
    public List<ServiceCatalogResponse> getAllActiveServices() {
        return servicesRepository.findByIsActiveTrue()
                .stream()
                .map(servicesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceCatalogResponse getServiceById(Long serviceId) {
        AdditionalServices service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId));
        return servicesMapper.toResponse(service);
    }

    @Override
    public List<ServiceCatalogResponse> getServicesByCategory(String category) {
        return servicesRepository.findByCategoryAndIsActiveTrue(category)
                .stream()
                .map(servicesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ServiceCatalogResponse> searchServices(String keyword, Pageable pageable) {
        return servicesRepository.findByServiceNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable)
                .map(servicesMapper::toResponse);
    }

    @Override
    public Page<ServiceCatalogResponse> getAllServices(Pageable pageable) {
        return servicesRepository.findAll(pageable)
                .map(servicesMapper::toResponse);
    }

    @Override
    @Transactional
    public ServiceCatalogResponse createService(ServiceCatalogRequest request) {
        // Validate
        if (request.getServiceName() == null || request.getServiceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống");
        }
        if (request.getPrice() == null || request.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Giá dịch vụ không hợp lệ");
        }

        AdditionalServices service = servicesMapper.toEntity(request);
        service = servicesRepository.save(service);
        return servicesMapper.toResponse(service);
    }

    @Override
    @Transactional
    public ServiceCatalogResponse updateService(Long serviceId, ServiceCatalogRequest request) {
        AdditionalServices service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId));

        servicesMapper.updateEntity(service, request);
        service = servicesRepository.save(service);
        return servicesMapper.toResponse(service);
    }

    @Override
    @Transactional
    public void deactivateService(Long serviceId) {
        AdditionalServices service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId));
        service.setIsActive(false);
        servicesRepository.save(service);
    }

    @Override
    @Transactional
    public void activateService(Long serviceId) {
        AdditionalServices service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId));
        service.setIsActive(true);
        servicesRepository.save(service);
    }

    @Override
    @Transactional
    public void deleteService(Long serviceId) {
        if (!servicesRepository.existsById(serviceId)) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId);
        }
        servicesRepository.deleteById(serviceId);
    }
}

