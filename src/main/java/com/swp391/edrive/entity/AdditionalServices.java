package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "additional_services")
public class AdditionalServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false, length = 100)
    private String serviceName;  // Tên dịch vụ (VD: "Phim cách nhiệt cao cấp", "Bộ sạc Wallbox 7kW")

    @Column(columnDefinition = "TEXT")
    private String description;  // Mô tả chi tiết dịch vụ

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal price;  // Giá của dịch vụ

    @Column(nullable = false)
    private Boolean isActive = true;  // Trạng thái dịch vụ (có đang hoạt động không)

    @Column(length = 50)
    private String category;  // Danh mục dịch vụ (VD: "Bảo vệ", "Sạc điện", "Bảo hành", "Camera")

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
