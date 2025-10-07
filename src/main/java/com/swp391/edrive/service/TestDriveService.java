package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.enums.TestDriveStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TestDriveService {
    TestDriveResponse book(TestDriveBookingRequest request);
    List<LocalTime> getAvailableSlots(Long dealerId, LocalDate date);
    TestDriveResponse cancel(Long testdriveId, String reason);
    TestDriveStatus getStatus(Long testdriveId);
    TestDriveResponse getById(Long testdriveId);
}
