package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.service.TestDriveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
}
