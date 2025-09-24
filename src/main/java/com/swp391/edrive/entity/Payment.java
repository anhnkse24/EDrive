package com.swp391.edrive.entity;

import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Double amount;
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
}
