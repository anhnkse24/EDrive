package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.request.TestDriveStatusManagerRequest;
import com.swp391.edrive.dto.request.TestDriveStatusStaffRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testdrives")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "Test Drive Management", description = "Quản lý lịch lái thử xe (CRUD)")
public class TestDriveController {

    private final TestDriveService testDriveService;

    @Operation(summary = "Lấy danh sách tất cả lịch lái thử")
    @GetMapping
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<List<TestDriveResponse>>> getAllTestDrives() {
        List<TestDriveResponse> list = testDriveService.getAllTestDrives();
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Lấy danh sách lịch lái thử thành công", list)
        );
    }

    @Operation(summary = "Lấy danh sách lịch lái thử theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<List<TestDriveResponse>>> getTestDrivesByDealerId(
            @PathVariable Long dealerId) {

        List<TestDriveResponse> list = testDriveService.getTestDrivesByDealerId(dealerId);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Lấy danh sách lịch lái thử thành công theo Dealer ID", list)
        );
    }

    @Operation(summary = "Tạo mới lịch lái thử cho Dealer cụ thể")
    @PostMapping("/dealer/{dealerId}")
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<TestDriveResponse>> createTestDriveByDealer(
            @PathVariable Long dealerId,
            @Valid @RequestBody TestDriveRequest request) {

        TestDriveResponse created = testDriveService.createTestDriveByDealer(dealerId, request);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Tạo lịch lái thử cho Dealer thành công", created)
        );
    }

    @Operation(summary = "Cập nhật lịch lái thử theo Dealer ID")
    @PutMapping("/dealer/{dealerId}/{testDriveId}")
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<TestDriveResponse>> updateTestDriveByDealer(
            @PathVariable Long dealerId,
            @PathVariable Long testDriveId,
            @Valid @RequestBody TestDriveRequest request) {

        TestDriveResponse updated = testDriveService.updateTestDriveByDealer(dealerId, testDriveId, request);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Cập nhật lịch lái thử cho Dealer thành công", updated)
        );
    }

    @Operation(summary = "Xóa lịch lái thử theo Dealer ID")
    @DeleteMapping("/dealer/{dealerId}/{testDriveId}")
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<Void>> deleteTestDriveByDealer(
            @PathVariable Long dealerId,
            @PathVariable Long testDriveId) {

        testDriveService.deleteTestDriveByDealer(dealerId, testDriveId);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Xóa lịch lái thử cho Dealer thành công", null)
        );
    }

    @Operation(summary = "Manager - Thay đổi trạng thái lịch lái thử")
    @PatchMapping("/dealer/{dealerId}/{testDriveId}/manager/status")
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasRole('DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<TestDriveResponse>> changeStatusByManager(
            @PathVariable Long dealerId,
            @PathVariable Long testDriveId,
            @Valid @RequestBody TestDriveStatusManagerRequest request
    ) {
        TestDriveResponse response =
                testDriveService.changeTestDriveStatusForManager(dealerId, testDriveId, request);

        return ResponseEntity.ok(
                new ResponseObject<>(200, "Manager thay đổi trạng thái thành công", response)
        );
    }

    @Operation(summary = "Staff - Thay đổi trạng thái lịch lái thử")
    @PatchMapping("/dealer/{dealerId}/{testDriveId}/staff/status")
    @SecurityRequirement(name = "api")
    @PreAuthorize("hasRole('DEALER_STAFF')")
    public ResponseEntity<ResponseObject<TestDriveResponse>> changeStatusByStaff(
            @PathVariable Long dealerId,
            @PathVariable Long testDriveId,
            @Valid @RequestBody TestDriveStatusStaffRequest request
    ) {
        TestDriveResponse response =
                testDriveService.changeTestDriveStatusForStaff(
                        request.getStaffUserId(),   // đúng nhất vì service yêu cầu userId
                        dealerId,
                        testDriveId,
                        request
                );

        return ResponseEntity.ok(
                new ResponseObject<>(200, "Staff thay đổi trạng thái thành công", response)
        );
    }
}
