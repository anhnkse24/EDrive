package com.swp391.edrive.entity;

import com.swp391.edrive.enums.PromotionStatus;
import com.swp391.edrive.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "promotions",
        indexes = {
                @Index(name = "idx_pm_dealer", columnList = "dealer_id"),
                @Index(name = "idx_pm_version", columnList = "version_id"),
                @Index(name = "idx_pm_version_color", columnList = "version_color_id"),
                @Index(name = "idx_pm_valid", columnList = "valid_from, valid_to")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private VehicleVersion version;

    @ManyToOne
    @JoinColumn(name = "version_color_id")
    private VersionColor versionColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", length = 20, nullable = false)
    private PromotionType discountType = PromotionType.AMOUNT;

    /** Giá trị khuyến mãi (VND cho AMOUNT, % cho PERCENT) */
    @Column(name = "discount_value", precision = 14, scale = 2, nullable = false)
    private BigDecimal discountValue = BigDecimal.ZERO;

    /** Thời gian hiệu lực */
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    /** Trạng thái khuyến mãi: ACTIVE / INACTIVE / EXPIRED */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PromotionStatus status = PromotionStatus.ACTIVE;

    @Column(length = 255)
    private String description;

}
