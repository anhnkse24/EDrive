package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.service.TestDriveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/test-drive")
@RequiredArgsConstructor
public class TestDriveController {
    private final TestDriveService testDriveService;

    @PostMapping("/book")
    public TestDriveResponse book(@Valid @RequestBody TestDriveBookingRequest request) {
        return testDriveService.book(request);
    }

    @GetMapping("/available")
    public List<LocalTime> available(
            @RequestParam Long dealerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return testDriveService.getAvailableSlots(dealerId, date);
    }

    @GetMapping
    public List<TestDriveResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long dealerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (dealerId != null && date != null) {
            return testDriveService.listByDealerAndDate(dealerId, date, page, size);
        } else if (dealerId != null) {
            return testDriveService.listByDealer(dealerId, page, size);
        } else {
            return testDriveService.list(page, size);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        try {
            TestDriveResponse td = testDriveService.getById(id);
            return ResponseEntity.ok(new ResponseObject(200, "Thông tin lịch lái thử", td));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    @PostMapping("/{id}/cancel")
    public TestDriveResponse cancel(
            @PathVariable("id") Long id,
            @RequestParam(required = false, defaultValue = "User requested") String reason
    ) {
        return testDriveService.cancel(id, reason);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ResponseObject> complete(@PathVariable Long id) {
        try {
            TestDriveResponse result = testDriveService.complete(id);
            return ResponseEntity.ok(new ResponseObject(200, "Đánh dấu hoàn thành thành công", result));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, ex.getMessage(), null));
        }
    }
}
