package com.swp391.edrive.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pricing_policies")
public class PricingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private Double wholesalePrice;
    private Float discountRate;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String status;
}
