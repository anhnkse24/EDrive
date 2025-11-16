package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.TestDriveStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestDriveRequest {

    @NotNull(message = "customerId không được để trống")
    private Long customerId;

    @NotNull(message = "dealerId không được để trống")
    private Long dealerId;

    @NotNull(message = "vehicleId không được để trống")
    private Long vehicleId;

    @NotNull(message = "Thời gian lái thử không được để trống")
    @Future(message = "Thời gian lái thử phải ở tương lai")
    private LocalDateTime scheduleDatetime;

    private TestDriveStatus status = TestDriveStatus.PENDING;

    @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
    private String cancelReason;
}
