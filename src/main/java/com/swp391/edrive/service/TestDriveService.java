package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TestDriveService {
    TestDriveResponse book(TestDriveBookingRequest request);
    List<LocalTime> getAvailableSlots(Long dealerId, Long versionId, Long versionColorId, LocalDate date);    TestDriveResponse cancel(Long testdriveId, String reason);
    TestDriveResponse getById(Long testdriveId);

    List<TestDriveResponse> list(int page, int size);

    List<TestDriveResponse> listByDealer(Long dealerId, int page, int size);

    List<TestDriveResponse> listByDealerAndDate(Long dealerId, LocalDate date, int page, int size);

    TestDriveResponse complete(Long testdriveId);

}
