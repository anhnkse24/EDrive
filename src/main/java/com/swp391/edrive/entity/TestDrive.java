package com.swp391.edrive.entity;

import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.enums.TestDriveStatusForStaff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drives")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testdriveId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "dealer_inventory_id")
    private DealerInventory dealerInventory;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String cancelReason;

    private LocalDateTime scheduleDatetime;

    @Enumerated(EnumType.STRING)
    private TestDriveStatus status;

    @Enumerated(EnumType.STRING)
    private TestDriveStatusForStaff statusForStaff;

    public TestDrive(Customer customer, Dealer dealer, Vehicle vehicle, LocalDateTime scheduleDatetime, TestDriveStatus status,  TestDriveStatusForStaff statusForStaff) {
        this.customer = customer;
        this.dealer = dealer;
        this.vehicle = vehicle;
        this.scheduleDatetime = scheduleDatetime;
        this.status = status;
        this.statusForStaff = statusForStaff;
    }
}