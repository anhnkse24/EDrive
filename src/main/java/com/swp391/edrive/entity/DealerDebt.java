package com.swp391.edrive.entity;

import com.swp391.edrive.enums.DebtStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dealer_debts")
public class DealerDebt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debtId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    private Double amount;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private DebtStatus status;
}
