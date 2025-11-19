package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "quotation_services")
public class QuotationServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private AdditionalServices service;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal priceAtSelection;  // Giá tại thời điểm chọn (lưu lại để tránh thay đổi giá ảnh hưởng)

    private Integer quantity = 1;  // Số lượng (mặc định là 1)

    @Column(columnDefinition = "TEXT")
    private String note;  // Ghi chú đặc biệt cho dịch vụ này
}

