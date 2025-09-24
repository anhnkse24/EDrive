package com.swp391.edrive.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private Integer quantity;

    private LocalDateTime lastUpdated;
}
