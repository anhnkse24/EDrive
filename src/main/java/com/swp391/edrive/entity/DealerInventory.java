package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dealer_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealerInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dealerInventoryId;

    @ManyToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Integer quantity;

    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "dealerInventory")
    private List<TestDrive> testDrives;
}