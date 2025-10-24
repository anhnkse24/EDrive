package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testdrives")
@RequiredArgsConstructor
@Tag(name = "Test Drive Management", description = "Quản lý lịch lái thử xe (CRUD)")
public class TestDriveController {

    private final TestDriveService testDriveService;

    @Operation(summary = "Tạo mới lịch lái thử")
    @PostMapping
    public ResponseEntity<ResponseObject> createTestDrive(@Valid @RequestBody TestDriveRequest request) {
        TestDriveResponse created = testDriveService.createTestDrive(request);
        return ResponseEntity.ok(new ResponseObject(200, "Tạo lịch lái thử thành công", created));
    }

    @Operation(summary = "Cập nhật lịch lái thử")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateTestDrive(@PathVariable Long id,
                                                          @Valid @RequestBody TestDriveRequest request) {
        TestDriveResponse updated = testDriveService.updateTestDrive(id, request);
        return ResponseEntity.ok(new ResponseObject(200, "Cập nhật lịch lái thử thành công", updated));
    }

    @Operation(summary = "Xóa lịch lái thử")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteTestDrive(@PathVariable Long id) {
        testDriveService.deleteTestDrive(id);
        return ResponseEntity.ok(new ResponseObject(200, "Xóa lịch lái thử thành công", null));
    }

    @Operation(summary = "Lấy thông tin lịch lái thử theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getTestDriveById(@PathVariable Long id) {
        TestDriveResponse response = testDriveService.getTestDriveById(id);
        return ResponseEntity.ok(new ResponseObject(200, "Lấy thông tin thành công", response));
    }

    @Operation(summary = "Lấy danh sách tất cả lịch lái thử")
    @GetMapping
    public ResponseEntity<ResponseObject> getAllTestDrives() {
        List<TestDriveResponse> list = testDriveService.getAllTestDrives();
        return ResponseEntity.ok(new ResponseObject(200, "Lấy danh sách lịch lái thử thành công", list));
    }
}
