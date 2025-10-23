package com.swp391.edrive.entity;

import com.swp391.edrive.enums.DebtStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dealer_debts")
public class DealerDebt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debtId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @Column(name = "amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private DebtStatus status = DebtStatus.PENDING;

    @OneToMany(mappedBy = "dealerDebt")
    private List<Payment> payments;
}
