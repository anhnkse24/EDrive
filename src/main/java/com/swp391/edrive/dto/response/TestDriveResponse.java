package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.TestDriveStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TestDriveResponse {
    private Long testdriveId;
    private Long customerId;
    private String customerName;
    private Long dealerId;
    private String dealerName;
    private Long vehicleId;
    private String vehicleModel;
    private LocalDateTime scheduleDatetime;
    private LocalDateTime completedAt;
    private TestDriveStatus status;
    private String cancelReason;
}
