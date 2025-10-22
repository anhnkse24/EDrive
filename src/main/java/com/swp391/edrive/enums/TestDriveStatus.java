package com.swp391.edrive.enums;

public enum TestDriveStatus {
    PENDING,      // KH vừa đặt, chờ xác nhận
    CONFIRMED,    // Đại lý xác nhận
    IN_PROGRESS,  // Đang lái thử
    COMPLETED,    // Hoàn tất
    CANCELLED,    // Huỷ
    NO_SHOW       // KH không đến
}
