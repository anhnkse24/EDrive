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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    private String modelName;
    private String version;
    private Integer batteryCapacityKwh;
    private Integer rangeKm;
    private Integer maxSpeedKmh;

    @Column(name = "charging_time_hours", nullable = false)
    private Float chargingTimeHours;

    private Integer seatingCapacity;
    private Integer motorPowerKw;
    private Integer weightKg;
    private Integer lengthMm;
    private Integer widthMm;
    private Integer heightMm;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @Column(precision = 12, scale = 2)
    private BigDecimal priceRetail;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @OneToMany(mappedBy = "vehicle")
    private List<TestDrive> testDrives;

    @OneToMany(mappedBy = "vehicle")
    private List<Quotation> quotations;

    @OneToMany(mappedBy = "vehicle")
    private List<ManufacturerInventory> manufacturerInventories;

    @OneToMany(mappedBy = "vehicle")
    private List<DealerInventory> dealerInventories;

    @OneToMany(mappedBy = "vehicle")
    private List<OrderItem> orderItems;

    @ManyToMany(mappedBy = "vehicles")
    private Set<Promotion> promotions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

}