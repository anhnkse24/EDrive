package com.swp391.edrive.entity;

import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.PromoTarget;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotions")
@Getter
@Setter
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

    // Gắn với Vehicle
    @ManyToMany
    @JoinTable(
            name = "vehicle_promotion",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private Set<Vehicle> vehicles = new HashSet<>();
}