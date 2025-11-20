package com.swp391.edrive.enums;

public enum ContractStatus {
    DRAFT,      // Admin tạo HĐ
    SIGNING,    // Hãng đã ký, đang chờ đại lý ký
    ACTIVE,     // Cả 2 bên đã ký, HĐ có hiệu lực
    REJECTED    // Đã từ chối
}