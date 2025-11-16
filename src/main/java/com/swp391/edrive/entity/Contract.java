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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

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
    private String contractCode;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    private String vehicleModel;
    private String vehicleVersion;
    private String colorName;
    private BigDecimal discountRate;
    private String terms;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "pdf_file")
    private byte[] pdfFile;

    @Column(name = "pdf_filename")
    private String pdfFilename;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "pdf_uploaded_at")
    private LocalDateTime pdfUploadedAt;

    private String manufacturerNote;

    // Digital signature fields
    @Lob
    @Column(name = "dealer_signature")
    private String dealerSignature;

    @Column(name = "dealer_signed_at")
    private LocalDateTime dealerSignedAt;

    @Lob
    @Column(name = "manufacturer_signature")
    private String manufacturerSignature;

    @Column(name = "manufacturer_signed_at")
    private LocalDateTime manufacturerSignedAt;

    // Payment receipt fields
    @Column(name = "payment_receipt_filename")
    private String paymentReceiptFilename;

    @Column(name = "payment_receipt_url")
    private String paymentReceiptUrl;

    @Column(name = "payment_receipt_uploaded_at")
    private LocalDateTime paymentReceiptUploadedAt;

    @Column(name = "payment_verified_at")
    private LocalDateTime paymentVerifiedAt;

    @Column(name = "payment_verified_by")
    private String paymentVerifiedBy;

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
