package com.swp391.edrive.entity;

import com.swp391.edrive.enums.DebtStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer_debts")
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

    private Double amount;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private DebtStatus status;
}
