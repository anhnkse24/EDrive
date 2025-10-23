package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "quotation_items",
        indexes = {
                @Index(name = "idx_qti_quotation", columnList = "quotation_id"),
                @Index(name = "idx_qti_version_color", columnList = "version_color_id")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class QuotationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Báo giá cha */
    @ManyToOne(optional = false)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    /** Trỏ vào VersionColor nếu bạn cần liên kết sản phẩm cụ thể (có thể null nếu chỉ dùng snapshot text) */
    @ManyToOne
    @JoinColumn(name = "version_color_id")
    private VersionColor versionColor;

    // --------- Snapshot để render nhanh, tránh JOIN nặng ----------
    @Column(name = "model_name", length = 120)
    private String modelName;

    @Column(name = "version_name", length = 120)
    private String versionName;

    @Column(name = "color_name", length = 60)
    private String colorName;

    /** Số lượng */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /** Đơn giá (snapshot) */
    @Column(name = "unit_price", precision = 14, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /** Thành tiền dòng = unitPrice * quantity (snapshot) */
    @Column(name = "line_total", precision = 14, scale = 2, nullable = false)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    /** Tự tính lineTotal khi lưu/sửa */
    @PrePersist @PreUpdate
    public void calcLineTotal() {
        if (unitPrice == null) unitPrice = BigDecimal.ZERO;
        if (quantity == null || quantity < 1) quantity = 1;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
