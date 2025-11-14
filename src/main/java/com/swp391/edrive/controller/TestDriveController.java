package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<ResponseObject> getAllTestDrives() {
        List<TestDriveResponse> list = testDriveService.getAllTestDrives();
        return ResponseEntity.ok(new ResponseObject(200, "Lấy danh sách lịch lái thử thành công", list));
    }


    @Operation(summary = "Lấy danh sách lịch lái thử theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    @SecurityRequirement(name = "api")

    public ResponseEntity<ResponseObject> getTestDrivesByDealerId(@PathVariable Long dealerId) {
        List<TestDriveResponse> list = testDriveService.getTestDrivesByDealerId(dealerId);
        return ResponseEntity.ok(new ResponseObject(200, "Lấy danh sách lịch lái thử thành công theo Dealer ID", list));
    }

    @Operation(summary = "Cập nhật lịch lái thử theo Dealer ID")
    @PutMapping("/dealer/{dealerId}/{testDriveId}")
    @SecurityRequirement(name = "api")

    public ResponseEntity<ResponseObject> updateTestDriveByDealer(@PathVariable Long dealerId,
                                                                  @PathVariable Long testDriveId,
                                                                  @Valid @RequestBody TestDriveRequest request) {
        TestDriveResponse updated = testDriveService.updateTestDriveByDealer(dealerId, testDriveId, request);
        return ResponseEntity.ok(new ResponseObject(200, "Cập nhật lịch lái thử cho Dealer thành công", updated));
    }

    @Operation(summary = "Xóa lịch lái thử theo Dealer ID")
    @DeleteMapping("/dealer/{dealerId}/{testDriveId}")
    @SecurityRequirement(name = "api")

    public ResponseEntity<ResponseObject> deleteTestDriveByDealer(@PathVariable Long dealerId,
                                                                  @PathVariable Long testDriveId) {
        testDriveService.deleteTestDriveByDealer(dealerId, testDriveId);
        return ResponseEntity.ok(new ResponseObject(200, "Xóa lịch lái thử cho Dealer thành công", null));
    }
    @Operation(summary = "Khách hàng đăng ký lái thử xe")
    @PostMapping("/customer")
    public ResponseEntity<ResponseObject> createByCustomer(@RequestBody TestDriveRequest request) {
        TestDriveResponse res = testDriveService.createTestDriveByCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Tạo lịch lái thử thành công", res));
    }

    @Operation(summary = "Dealer duyệt lịch lái thử")
    @PutMapping("/{dealerId}/{testDriveId}/approve")
    public ResponseEntity<ResponseObject> approve(
            @PathVariable Long dealerId,
            @PathVariable Long testDriveId) {

        TestDriveResponse res = testDriveService.approveTestDrive(dealerId, testDriveId);
        return ResponseEntity.ok(new ResponseObject(200, "Duyệt lịch lái thử thành công", res));
    }

    @Operation(summary = "Dealer xác nhận khách hàng đã lái thử xong")
    @PutMapping("/{dealerId}/{testDriveId}/complete")
    public ResponseEntity<ResponseObject> complete(
            @PathVariable Long dealerId,
            @PathVariable Long testDriveId) {

        TestDriveResponse res = testDriveService.completeTestDrive(dealerId, testDriveId);
        return ResponseEntity.ok(new ResponseObject(200, "Hoàn thành buổi lái thử", res));
    }
    @PutMapping("/{testDriveId}/cancel")
    public ResponseEntity<ResponseObject> cancelTestDrive(
            @PathVariable Long testDriveId,
            @RequestParam(required = false) String reason
    ) {
        TestDriveResponse res = testDriveService.cancelTestDrive(testDriveId, reason);
        return ResponseEntity.ok(new ResponseObject(200, "Đã hủy lịch lái thử", res));
    }
}
