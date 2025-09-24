package com.swp391.edrive.entity;

import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.PromoTarget;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promoId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Double discountValue;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PromoTarget applicableTo;
}
