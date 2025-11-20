// VersionColor.java
package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="version_colors",
        uniqueConstraints = @UniqueConstraint(columnNames = {"version_id","color_code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VersionColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Phiên bản cha */
    @ManyToOne(optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    @ToString.Exclude
    private VehicleVersion version;

    /** Tên màu hiển thị (VD: Trắng ngọc trai) */
    @Column(name = "color_name", nullable = false, length = 60)
    private String colorName;

    /** Mã màu nội bộ (VD: WHT-PEARL) */
    @Column(name = "color_code", length = 40)
    private String colorCode;

    /** Giá chênh lệch (+/-) so với basePrice */
    @Column(name = "price_delta", precision = 14, scale = 2)
    private BigDecimal priceDelta;

    /** Nếu có giá riêng, bỏ qua basePrice */
    @Column(name = "price_override", precision = 14, scale = 2)
    private BigDecimal priceOverride;

    /** Ảnh đại diện cho màu */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /** Còn kinh doanh không */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // --- Helper ---
    @Transient
    public BigDecimal retailPrice() {
        if (priceOverride != null) return priceOverride;
        BigDecimal base = version != null && version.getBasePrice() != null ? version.getBasePrice() : BigDecimal.ZERO;
        BigDecimal delta = priceDelta != null ? priceDelta : BigDecimal.ZERO;
        return base.add(delta);
    }
}
