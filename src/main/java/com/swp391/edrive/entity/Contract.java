package com.swp391.edrive.entity;

import com.swp391.edrive.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @OneToOne
    @JoinColumn(name = "order_id") // sẽ gắn sau ở bước Payment/Fulfillment
    private Order order;

    @OneToOne(optional = false)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation; // nối từ Quotation APPROVED

    @Column(name = "signed_at")
    private LocalDateTime signedAt; // chỉ set khi ký

    @Column(name = "contract_value", precision = 14, scale = 2, nullable = false)
    private BigDecimal contractValue = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status = ContractStatus.DRAFT; // DRAFT -> ACTIVE -> COMPLETED/TERMINATED/CANCELLED
}
