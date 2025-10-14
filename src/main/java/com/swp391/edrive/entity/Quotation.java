package com.swp391.edrive.entity;

import com.swp391.edrive.enums.QuotationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "quotations",
        indexes = {
                @Index(name = "idx_qt_dealer", columnList = "dealer_id"),
                @Index(name = "idx_qt_customer", columnList = "customer_id"),
                @Index(name = "idx_qt_created_at", columnList = "created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_qt_code", columnNames = {"quote_code"})
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Đại lý phát hành báo giá */
    @ManyToOne(optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    /** Khách hàng nhận báo giá */
    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** Thời điểm tạo báo giá */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Hạn hiệu lực của báo giá (tùy chọn) */
    @Column(name = "valid_until")
    private LocalDate validUntil;

    /** Trạng thái báo giá (DRAFT/SENT/APPROVED/EXPIRED/CANCELLED) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private QuotationStatus status = QuotationStatus.DRAFT;

    /** Tổng tiền snapshot = sum(lineTotal) của các items */
    @Column(name = "grand_total", precision = 14, scale = 2, nullable = false)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    /** Ghi chú (tùy chọn) */
    @Column(length = 255)
    private String note;

    /** Danh sách dòng hàng */
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<QuotationItem> items = new ArrayList<>();

    // --------- Helpers ---------
    public void addItem(QuotationItem item) {
        item.setQuotation(this);
        this.items.add(item);
        recomputeTotals();
    }

    public void removeItem(QuotationItem item) {
        this.items.remove(item);
        item.setQuotation(null);
        recomputeTotals();
    }

    /** Tính lại tổng tiền từ các dòng item (snapshot) */
    public void recomputeTotals() {
        this.grandTotal = this.items.stream()
                .map(QuotationItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        // Nếu muốn mặc định 7 ngày hiệu lực, mở dòng dưới:
        // if (this.validUntil == null) this.validUntil = LocalDate.now().plusDays(7);
        recomputeTotals();
    }

    @PreUpdate
    public void preUpdate() {
        recomputeTotals();
    }
}
