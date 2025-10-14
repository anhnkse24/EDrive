package com.swp391.edrive.entity;

import com.swp391.edrive.enums.ContractStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @ManyToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "signed_at", nullable = false)
    private LocalDateTime signedAt = LocalDateTime.now();

    @Column(name = "contract_value", precision = 14, scale = 2, nullable = false)
    private BigDecimal contractValue = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status = ContractStatus.DRAFT;
}
