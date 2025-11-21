package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.StaffCreateRequest;
import com.swp391.edrive.dto.request.StaffUpdateRequest;
import com.swp391.edrive.dto.response.StaffResponse;

import java.util.List;

public interface StaffService {
    // Dealer Manager creates Dealer Staff
    StaffResponse createDealerStaff(StaffCreateRequest request);

    // Admin creates EVM Staff
    StaffResponse createEvmStaff(StaffCreateRequest request);

    // Get staff by ID
    StaffResponse getStaffById(Long staffId);

    // Get all staff by dealer (for DEALER_MANAGER)
    List<StaffResponse> getAllDealerStaff();

    // Get all EVM staff (for ADMIN)
    List<StaffResponse> getAllEvmStaff();

    // Update staff
    StaffResponse updateStaff(Long staffId, StaffUpdateRequest request);

    // Delete staff (hard delete - xóa hẳn khỏi database)
    void deleteStaff(Long staffId);
}

