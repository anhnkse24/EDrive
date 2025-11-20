package com.swp391.edrive.entity;

import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.QuotationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal unitPrice;        // giá xe tại thời điểm báo giá

    private Integer installmentMonths; // Tháng trả góp, chỉ áp dụng khi trả góp
    private BigDecimal monthlyInstallment;  // Số tiền trả hàng tháng, chỉ áp dụng khi trả góp

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuotationStatus quotationStatus; // Trạng thái báo giá

    // ====== Giá sau khuyến mãi ======
    @Column(precision = 14, scale = 2)
    private BigDecimal promotionDiscountAmount; // Tổng giảm giá từ khuyến mãi

    @Column(precision = 14, scale = 2)
    private BigDecimal priceAfterPromotion;    // Giá trị sau khi trừ khuyến mãi

    @ManyToMany
    @JoinTable(
            name = "quotation_promotion",
            joinColumns = @JoinColumn(name = "quotation_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private Set<Promotion> promotions;

    // ====== Dịch vụ bổ sung ======
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuotationServices> quotationServices;

    // ====== Ngày tạo và ngày hết hạn báo giá ======
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate expiryDate;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (quotationStatus == null) {
            quotationStatus = QuotationStatus.PENDING;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
