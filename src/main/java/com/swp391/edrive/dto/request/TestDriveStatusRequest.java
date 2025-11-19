package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.enums.TestDriveStatusForStaff;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TestDriveStatusRequest {

    @NotNull(message = "Status không được để trống")
    private TestDriveStatus status;

    @NotNull(message = "StatusForStaff không được để trống")
    private TestDriveStatusForStaff statusForStaff;

    @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
    private String cancelReason;
}

