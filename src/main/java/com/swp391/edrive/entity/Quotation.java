package com.swp391.edrive.entity;

import com.swp391.edrive.enums.QuotationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
@Getter
@Setter
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quotationId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    private Double quotedPrice;
    // ====== Giá & chiết khấu ======
    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal unitPrice;        // giá xe tại thời điểm báo giá

    @Column(precision = 5, scale = 2)
    private BigDecimal discountRate;     // ví dụ 0.05 (5%)
    @Column(precision = 14, scale = 2)
    private BigDecimal discountAmount;   // unitPrice * discountRate

    // ====== Dịch vụ chính hãng ======
    @Column(nullable = false)
    private boolean includeInsurancePercent;   // +3% giá xe
    @Column(nullable = false)
    private boolean includeWarrantyExtension;  // +50,000,000
    @Column(nullable = false)
    private boolean includeAccessories;        // +30,000,000

    @Column(precision = 14, scale = 2)
    private BigDecimal serviceTotal;           // tổng tiền dịch vụ đã chọn

    // ====== Thuế & tổng tiền ======
    @Column(precision = 5, scale = 2)
    private BigDecimal vatRate;                // ví dụ 0.10
    @Column(precision = 14, scale = 2)
    private BigDecimal vehicleSubtotal;        // đơn giá xe
    @Column(precision = 14, scale = 2)
    private BigDecimal subtotalAfterDiscount;  // sau chiết khấu
    @Column(precision = 14, scale = 2)
    private BigDecimal taxableBase;            // sau chiết khấu + dịch vụ
    @Column(precision = 14, scale = 2)
    private BigDecimal vatAmount;
    @Column(precision = 14, scale = 2)
    private BigDecimal grandTotal;

    // ====== Thông tin khách hàng ======
    @Column(length = 150)
    private String customerFullName;
    @Column(length = 30)
    private String phone;
    @Column(length = 150)
    private String email;
    @Column(length = 500)
    private String fullAddress;
    @Column(columnDefinition = "TEXT")
    private String notes;

    // ====== Audit ======
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
