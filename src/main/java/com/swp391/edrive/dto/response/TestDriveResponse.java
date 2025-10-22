package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.TestDriveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDriveResponse {
    private Long id;
    private Long customerId;
    private Long dealerId;
    private Long versionId;
    private Long versionColorId; 
    private LocalDateTime scheduledAt;
    private TestDriveStatus status;
    private LocalDateTime confirmedAt;
    private LocalDateTime checkInAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
}
