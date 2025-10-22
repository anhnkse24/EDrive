package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.enums.TestDriveStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TestDriveService {
    // Booking & slots
    TestDriveResponse book(TestDriveBookingRequest request);
    List<LocalTime> getAvailableSlots(Long dealerId, Long versionId, Long versionColorId, LocalDate date);

    // Get one
    TestDriveResponse getById(Long testdriveId);

    // Listing (đủ cả 2 phiên bản để tránh lỗi “must implement …”)
    List<TestDriveResponse> list(int page, int size);                                   // cũ
    List<TestDriveResponse> list(int page, int size, TestDriveStatus status);           // mới (lọc theo trạng thái)

    List<TestDriveResponse> listByDealer(Long dealerId, int page, int size, TestDriveStatus status); // mới

    List<TestDriveResponse> listByDealerAndDate(Long dealerId, LocalDate date, int page, int size, TestDriveStatus status); // mới

    // State transitions
    TestDriveResponse confirm(Long testdriveId);
    TestDriveResponse checkIn(Long testdriveId);
    TestDriveResponse complete(Long testdriveId);
    TestDriveResponse cancel(Long testdriveId, String reason);
    TestDriveResponse markNoShow(Long testdriveId);

}
