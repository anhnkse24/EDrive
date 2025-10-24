package com.swp391.edrive.enums;

public enum PaymentStatus {
    /** Mới tạo, chưa có giao dịch */
    PENDING,

    /** Đã khởi tạo thanh toán (VD: chờ VNPay callback). Cash-only thường bỏ qua trạng thái này. */
    PROCESSING,

    /** Đã thanh toán đầy đủ */
    PAID,

    /** Thanh toán thất bại hoặc bị hủy */
    FAILED
}
