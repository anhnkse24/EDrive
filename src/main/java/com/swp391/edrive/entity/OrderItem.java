package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 5, scale = 4)
    private BigDecimal discountRate;

    @Column(precision = 20, scale = 2)
    private BigDecimal discountAmount;

    @Column(precision = 20, scale = 2)
    private BigDecimal totalPrice;
}