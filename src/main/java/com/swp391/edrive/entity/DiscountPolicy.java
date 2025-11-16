package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "discount_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer minQuantity;

    @Column(nullable = false)
    private Integer maxQuantity;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate; // Ví dụ: 0.05 = 5%, 0.10 = 10%

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(length = 500)
    private String description; // Mô tả chính sách chiết khấu
}

