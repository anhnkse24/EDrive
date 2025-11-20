package com.swp391.edrive.repository;

import com.swp391.edrive.entity.AdditionalServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalServicesRepository extends JpaRepository<AdditionalServices, Long> {

    // Tìm tất cả dịch vụ đang hoạt động
    List<AdditionalServices> findByIsActiveTrue();

    // Tìm dịch vụ đang hoạt động với phân trang
    Page<AdditionalServices> findByIsActiveTrue(Pageable pageable);

    // Tìm dịch vụ theo category
    List<AdditionalServices> findByCategoryAndIsActiveTrue(String category);

    // Tìm dịch vụ theo tên (có hỗ trợ search)
    Page<AdditionalServices> findByServiceNameContainingIgnoreCaseAndIsActiveTrue(String serviceName, Pageable pageable);
}

