package com.swp391.edrive.entity;

import com.swp391.edrive.enums.VehicleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    private String modelName;
    private String version;
    private String color;

    private Integer batteryCapacityKwh;
    private Integer rangeKm;
    private Integer maxSpeedKmh;
    private Float chargingTimeHours;
    private Integer seatingCapacity;
    private Integer motorPowerKw;
    private Integer weightKg;
    private Integer lengthMm;
    private Integer widthMm;
    private Integer heightMm;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @Column(precision = 12, scale = 2)
    private BigDecimal priceRetail;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @OneToMany(mappedBy = "vehicle")
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "vehicle")
    private List<PricingPolicy> pricingPolicies;

    @OneToMany(mappedBy = "vehicle")
    private List<TestDrive> testDrives;

    @OneToMany(mappedBy = "vehicle")
    private List<Quotation> quotations;
}
