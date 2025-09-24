package com.swp391.edrive.entity;

import com.swp391.edrive.enums.TestDriveStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_drives")
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

    private LocalDateTime scheduleDatetime;

    @Enumerated(EnumType.STRING)
    private TestDriveStatus status;
}
