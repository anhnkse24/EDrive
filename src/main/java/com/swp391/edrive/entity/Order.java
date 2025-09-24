package com.swp391.edrive.entity;

import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDate orderDate;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
