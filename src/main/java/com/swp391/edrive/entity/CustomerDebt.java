package com.swp391.edrive.entity;

import com.swp391.edrive.enums.DebtStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "customer_debts",
        indexes = {
                @Index(name = "idx_cdebt_customer", columnList = "customer_id"),
                @Index(name = "idx_cdebt_order", columnList = "order_id"),
                @Index(name = "idx_cdebt_status", columnList = "status")
        }
)
@Getter
@Setter
public class CustomerDebt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debtId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DebtStatus status = DebtStatus.PENDING;
}
