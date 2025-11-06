package com.swp391.edrive.entity;

import com.swp391.edrive.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Column(nullable = false)
    private String contractCode; // Mã hợp đồng (VD: CT2025-0001)

    @Enumerated(EnumType.STRING)
    private ContractStatus status; // DRAFT, PENDING_MANUFACTURER, APPROVED, REJECTED

    @Column(nullable = false)
    private BigDecimal totalPrice;

    private String vehicleModel;
    private String vehicleVersion;
    private BigDecimal discountRate;
    private String terms; // điều khoản hợp đồng

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "pdf_uploaded_at")
    private LocalDateTime pdfUploadedAt;

    private String manufacturerNote;

    @PrePersist
    void onCreate() {
        if (status == null) status = ContractStatus.DRAFT;
        if (contractCode == null) {
            contractCode = "CT-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    .format(LocalDateTime.now()) + "-" + (int)(Math.random()*9000+1000);
        }
    }

    @PreUpdate
    void onUpdate() {
    }
}
