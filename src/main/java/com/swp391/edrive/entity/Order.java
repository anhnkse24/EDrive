package com.swp391.edrive.entity;

import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDate orderDate;
    private LocalDate desiredDeliveryDate;
    private LocalDate actualDeliveryDate;

    // Các trường tính toán
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal vatAmount;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String deliveryAddress;
    private String deliveryNote;

    @Column
    private String paymentImage;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order")
    private List<Payment> payments;

    @Column
    private LocalDateTime paymentExpiryTime;


    @PrePersist
    protected void onCreate() {
        orderDate = LocalDate.now();
        // Set payment expiry time to 10 minutes from creation
        paymentExpiryTime = LocalDateTime.now().plusMinutes(10);
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.CHỜ_DUYỆT;
        }

    }
}