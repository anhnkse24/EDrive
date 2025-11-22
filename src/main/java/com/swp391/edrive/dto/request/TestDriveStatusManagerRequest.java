package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.TestDriveStatusManager;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TestDriveStatusManagerRequest {
    private Long staffUserId;

    private TestDriveStatusManager statusOfManager;

    @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
    private String cancelReason;

}
