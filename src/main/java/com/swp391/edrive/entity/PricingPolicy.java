// PricingPolicy.java (sửa)
package com.swp391.edrive.entity;

import com.swp391.edrive.enums.PricingPolicyStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="pricing_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PricingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private VehicleVersion version;

    @ManyToOne
    @JoinColumn(name = "version_color_id")
    private VersionColor versionColor;

    /** Giá sỉ mà hãng bán cho đại lý (tính bằng VND) */
    @Column(name = "wholesale_price", precision = 14, scale = 2, nullable = false)
    private BigDecimal wholesalePrice = BigDecimal.ZERO;

    /** Tỷ lệ chiết khấu (%), ví dụ 5.5 = 5.5% */
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    /** Ngày bắt đầu hiệu lực */
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    /** Ngày hết hiệu lực */
    @Column(name = "valid_to")
    private LocalDate validTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PricingPolicyStatus status = PricingPolicyStatus.ACTIVE;
}
