package com.swp391.edrive.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TestDriveBookingRequest {
    private Long dealerId;
    private Long versionId;
    private Long versionColorId;

    private LocalDate date;
    private int hour;
    private int minute;

    private String fullName;
    private String phone;
    private String email;
    private String idCardNo;
}
