package com.swp391.edrive.enums;

public enum ContractStatus {
    DRAFT,                // Đại lý mới tạo, chưa gửi
    PENDING_MANUFACTURER, // Đã gửi cho hãng, chờ duyệt
    APPROVED,             // Hãng duyệt
    REJECTED              // Hãng từ chối
}
