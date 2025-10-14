// VehicleVersion.java
package com.swp391.edrive.entity;

import com.swp391.edrive.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "vehicle_versions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"model_id","version_name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Thuộc model nào */
    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private VehicleModel model;

    /** Tên phiên bản: Standard / Premium / Sport... */
    @Column(name = "version_name", nullable = false, length = 60)
    private String versionName;

    // ---- Thông số kỹ thuật (không phụ thuộc màu) ----
    private Integer batteryCapacityKwh;   // kWh
    private Integer rangeKm;              // km
    private Integer maxSpeedKmh;          // km/h
    private Float   chargingTimeHours;    // giờ
    private Integer seatingCapacity;      // số chỗ
    private Integer motorPowerKw;         // kW
    private Integer weightKg;             // kg
    private Integer lengthMm;             // mm
    private Integer widthMm;              // mm
    private Integer heightMm;             // mm

    /** Giá gốc của phiên bản (chưa tính phụ thu màu) */
    @Column(name = "base_price", precision = 14, scale = 2, nullable = false)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    /** AVAILABLE / DISCONTINUED / ... */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    /** Danh sách màu thuộc phiên bản */
    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<VersionColor> colors = new ArrayList<>();

    // ---- Helper: đảm bảo quan hệ 2 chiều và dễ seed ----
    public void addColor(VersionColor c) {
        if (c == null) return;
        c.setVersion(this);
        this.colors.add(c);
    }

    public void removeColor(VersionColor c) {
        if (c == null) return;
        this.colors.remove(c);
        c.setVersion(null);
    }
}
