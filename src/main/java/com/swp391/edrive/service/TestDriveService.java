package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;

import java.util.List;

public interface TestDriveService {
    TestDriveResponse createTestDrive(TestDriveRequest request);
    TestDriveResponse updateTestDrive(Long id, TestDriveRequest request);
    void deleteTestDrive(Long id);
    TestDriveResponse getTestDriveById(Long id);
    List<TestDriveResponse> getAllTestDrives();
    List<TestDriveResponse> getTestDrivesByDealerId(Long dealerId);

}
