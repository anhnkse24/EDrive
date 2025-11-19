package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.enums.TestDriveStatusForStaff;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TestDriveStatusRequest {

    private TestDriveStatus status;

    private TestDriveStatusForStaff statusForStaff;

    @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
    private String cancelReason;

    public void setStatus(String value) {
        if (value == null || value.isBlank()) {
            this.status = null;
        } else {
            this.status = TestDriveStatus.valueOf(value);
        }
    }
    public void setStatusForStaff(String value) {
        if (value == null || value.isBlank()) {
            this.statusForStaff = null;
        } else {
            this.statusForStaff = TestDriveStatusForStaff.valueOf(value);
        }
    }
}

