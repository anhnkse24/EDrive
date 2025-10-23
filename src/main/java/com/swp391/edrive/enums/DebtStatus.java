package com.swp391.edrive.enums;

public enum DebtStatus {
    PENDING,   // Đã phát sinh nhưng chưa đến hạn thanh toán
    UNPAID,    // Đến hạn nhưng chưa thanh toán
    PARTIAL,   // Đã thanh toán một phần
    PAID,      // Đã thanh toán đầy đủ
    OVERDUE    // Quá hạn thanh toán
}
