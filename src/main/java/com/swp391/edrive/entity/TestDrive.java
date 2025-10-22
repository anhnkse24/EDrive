package com.swp391.edrive.entity;

import com.swp391.edrive.enums.TestDriveStatus;
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
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private VehicleVersion version;

    @ManyToOne
    @JoinColumn(name = "version_color_id")
    private VersionColor versionColor; // nullable = true

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;  // thời gian hẹn lái thử

    @Column(name = "completed_at")
    private LocalDateTime completedAt;  // thời gian hoàn tất

    @Column(length = 500)
    private String cancelReason;

    private LocalDateTime confirmedAt;

    private LocalDateTime checkInAt;

    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TestDriveStatus status = TestDriveStatus.PENDING;

    // Helper constructor cho seed data
    public TestDrive(Customer customer, Dealer dealer, VehicleVersion version,
                     LocalDateTime scheduledAt, TestDriveStatus status) {
        this.customer = customer;
        this.dealer = dealer;
        this.version = version;
        this.scheduledAt = scheduledAt;
        this.status = status;
    }

}
