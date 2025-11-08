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

    @PrePersist
    void onCreate() {
        if (status == null) status = ContractStatus.BẢN_NHÁP;
        if (contractCode == null) {
            contractCode = "CT-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    .format(LocalDateTime.now()) + "-" + (int)(Math.random()*9000+1000);
        }
    }

    @PreUpdate
    void onUpdate() {
    }
}
