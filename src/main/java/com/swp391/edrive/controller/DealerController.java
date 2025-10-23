package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.DealerRequest;
import com.swp391.edrive.dto.response.DealerResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
@Tag(name = "Dealer Management", description = "Quản lý thông tin đại lý (Dealer CRUD)")
public class DealerController {

    private final DealerService dealerService;

    // 🟢 CREATE
    @Operation(summary = "Tạo mới đại lý")
    @PostMapping
    public ResponseEntity<ResponseObject> createDealer(@Valid @RequestBody DealerRequest request) {
        DealerResponse created = dealerService.createDealer(request);
        return ResponseEntity.ok(
                new ResponseObject(200, "Tạo đại lý thành công", created)
        );
    }

    // 🟡 UPDATE
    @Operation(summary = "Cập nhật thông tin đại lý")
    @PutMapping("/{dealerId}")
    public ResponseEntity<ResponseObject> updateDealer(
            @PathVariable Long dealerId,
            @Valid @RequestBody DealerRequest request) {
        DealerResponse updated = dealerService.updateDealer(dealerId, request);
        return ResponseEntity.ok(
                new ResponseObject(200, "Cập nhật đại lý thành công", updated)
        );
    }

    // 🔴 DELETE
    @Operation(summary = "Xóa đại lý theo ID")
    @DeleteMapping("/{dealerId}")
    public ResponseEntity<ResponseObject> deleteDealer(@PathVariable Long dealerId) {
        dealerService.deleteDealer(dealerId);
        return ResponseEntity.ok(
                new ResponseObject(200, "Xóa đại lý thành công", null)
        );
    }

    // 🔵 GET BY ID
    @Operation(summary = "Lấy thông tin chi tiết đại lý theo ID")
    @GetMapping("/{dealerId}")
    public ResponseEntity<ResponseObject> getDealerById(@PathVariable Long dealerId) {
        DealerResponse dealer = dealerService.getDealerById(dealerId);
        return ResponseEntity.ok(
                new ResponseObject(200, "Lấy thông tin đại lý thành công", dealer)
        );
    }

    // 🟣 GET ALL
    @Operation(summary = "Lấy danh sách tất cả đại lý")
    @GetMapping
    public ResponseEntity<ResponseObject> getAllDealers() {
        List<DealerResponse> list = dealerService.getAllDealers();
        return ResponseEntity.ok(
                new ResponseObject(200, "Lấy danh sách đại lý thành công", list)
        );
    }
}
