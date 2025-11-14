package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;

import java.util.List;

public interface TestDriveService {
    List<TestDriveResponse> getAllTestDrives();

    List<TestDriveResponse> getTestDrivesByDealerId(Long dealerId);
    TestDriveResponse createTestDriveByDealer(Long dealerId, TestDriveRequest request);
    TestDriveResponse updateTestDriveByDealer(Long dealerId, Long testDriveId, TestDriveRequest request);
    void deleteTestDriveByDealer(Long dealerId, Long testDriveId);
    TestDriveResponse createTestDriveByCustomer(TestDriveRequest request);
    TestDriveResponse approveTestDrive(Long dealerId, Long testDriveId);
    TestDriveResponse completeTestDrive(Long dealerId, Long testDriveId);
    TestDriveResponse cancelTestDrive(Long testDriveId, String reason);

}
