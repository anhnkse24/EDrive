package com.swp391.edrive.entity;

import com.swp391.edrive.enums.ContractStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDate contractDate;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;
}
