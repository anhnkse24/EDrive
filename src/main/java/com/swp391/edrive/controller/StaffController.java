package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.StaffCreateRequest;
import com.swp391.edrive.dto.request.StaffUpdateRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.StaffResponse;
import com.swp391.edrive.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff Management", description = "APIs for managing dealer staff and EVM staff")
@SecurityRequirement(name = "api")
public class StaffController {

    private final StaffService staffService;

    // ========== DEALER MANAGER APIs ==========

    @PostMapping("/dealer")
    @PreAuthorize("hasRole('DEALER_MANAGER')")
    @Operation(summary = "Tạo nhân viên đại lý", description = "Dealer Manager tạo tài khoản cho nhân viên đại lý")
    public ResponseEntity<ResponseObject<StaffResponse>> createDealerStaff(
            @Valid @RequestBody StaffCreateRequest request) {
        StaffResponse response = staffService.createDealerStaff(request);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Tạo nhân viên đại lý thành công",
                response
        ));
    }

    @GetMapping("/dealer")
    @PreAuthorize("hasRole('DEALER_MANAGER')")
    @Operation(summary = "Lấy danh sách nhân viên đại lý", description = "Dealer Manager xem tất cả nhân viên của đại lý mình")
    public ResponseEntity<ResponseObject<List<StaffResponse>>> getAllDealerStaff() {
        List<StaffResponse> staffList = staffService.getAllDealerStaff();
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Lấy danh sách nhân viên đại lý thành công",
                staffList
        ));
    }

    @GetMapping("/dealer/{staffId}")
    @PreAuthorize("hasRole('DEALER_MANAGER')")
    @Operation(summary = "Xem chi tiết nhân viên đại lý", description = "Dealer Manager xem thông tin chi tiết nhân viên")
    public ResponseEntity<ResponseObject<StaffResponse>> getDealerStaffById(@PathVariable Long staffId) {
        StaffResponse response = staffService.getStaffById(staffId);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Lấy thông tin nhân viên thành công",
                response
        ));
    }

    @PutMapping("/dealer/{staffId}")
    @PreAuthorize("hasRole('DEALER_MANAGER')")
    @Operation(summary = "Cập nhật thông tin nhân viên đại lý", description = "Dealer Manager cập nhật thông tin nhân viên")
    public ResponseEntity<ResponseObject<StaffResponse>> updateDealerStaff(
            @PathVariable Long staffId,
            @Valid @RequestBody StaffUpdateRequest request) {
        StaffResponse response = staffService.updateStaff(staffId, request);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Cập nhật thông tin nhân viên thành công",
                response
        ));
    }

    @DeleteMapping("/dealer/{staffId}")
    @PreAuthorize("hasRole('DEALER_MANAGER')")
    @Operation(summary = "Xóa nhân viên đại lý", description = "Dealer Manager xóa hẳn tài khoản nhân viên khỏi hệ thống (hard delete)")
    public ResponseEntity<ResponseObject<Void>> deleteDealerStaff(@PathVariable Long staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Xóa nhân viên thành công",
                null
        ));
    }

    // ========== ADMIN APIs ==========

    @PostMapping("/evm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo nhân viên EVM", description = "Admin tạo tài khoản cho nhân viên EVM")
    public ResponseEntity<ResponseObject<StaffResponse>> createEvmStaff(
            @Valid @RequestBody StaffCreateRequest request) {
        StaffResponse response = staffService.createEvmStaff(request);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Tạo nhân viên EVM thành công",
                response
        ));
    }

    @GetMapping("/evm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách nhân viên EVM", description = "Admin xem tất cả nhân viên EVM")
    public ResponseEntity<ResponseObject<List<StaffResponse>>> getAllEvmStaff() {
        List<StaffResponse> staffList = staffService.getAllEvmStaff();
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Lấy danh sách nhân viên EVM thành công",
                staffList
        ));
    }

    @GetMapping("/evm/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xem chi tiết nhân viên EVM", description = "Admin xem thông tin chi tiết nhân viên EVM")
    public ResponseEntity<ResponseObject<StaffResponse>> getEvmStaffById(@PathVariable Long staffId) {
        StaffResponse response = staffService.getStaffById(staffId);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Lấy thông tin nhân viên EVM thành công",
                response
        ));
    }

    @PutMapping("/evm/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin nhân viên EVM", description = "Admin cập nhật thông tin nhân viên EVM")
    public ResponseEntity<ResponseObject<StaffResponse>> updateEvmStaff(
            @PathVariable Long staffId,
            @Valid @RequestBody StaffUpdateRequest request) {
        StaffResponse response = staffService.updateStaff(staffId, request);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Cập nhật thông tin nhân viên EVM thành công",
                response
        ));
    }

    @DeleteMapping("/evm/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa nhân viên EVM", description = "Admin xóa hẳn tài khoản nhân viên EVM khỏi hệ thống (hard delete)")
    public ResponseEntity<ResponseObject<Void>> deleteEvmStaff(@PathVariable Long staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok(new ResponseObject<>(
                HttpStatus.OK.value(),
                "Xóa nhân viên EVM thành công",
                null
        ));
    }
}

