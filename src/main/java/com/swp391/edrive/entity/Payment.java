package com.swp391.edrive.entity;

import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20, nullable = false)
    private PaymentType paymentType = PaymentType.FULL;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 20, nullable = false)
    private PaymentMethod method = PaymentMethod.CASH;

    @ManyToOne @JoinColumn(name = "dealer_debt_id")
    private DealerDebt dealerDebt;

    // nếu có dùng nợ khách
    @ManyToOne @JoinColumn(name = "customer_debt_id")
    private CustomerDebt customerDebt;

    @Column(length = 255)
    private String note;
}
