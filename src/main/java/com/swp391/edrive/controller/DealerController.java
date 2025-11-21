package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.DealerRequest;
import com.swp391.edrive.dto.response.DealerResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.DealerService;
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
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
@Tag(name = "Dealer Management", description = "Quản lý thông tin đại lý (Dealer CRUD)")
@SecurityRequirement(name = "api")

public class DealerController {

    private final DealerService dealerService;

    @Operation(summary = "Tạo mới đại lý")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseObject<DealerResponse>> createDealer(
            @Valid @RequestBody DealerRequest request) {

        DealerResponse created = dealerService.createDealer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.<DealerResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Tạo đại lý thành công")
                        .data(created)
                        .build()
                );
    }

    @Operation(summary = "Cập nhật thông tin đại lý")
    @PutMapping("/{dealerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<DealerResponse>> updateDealer(
            @PathVariable Long dealerId,
            @Valid @RequestBody DealerRequest request) {

        DealerResponse updated = dealerService.updateDealer(dealerId, request);

        return ResponseEntity.ok(
                ResponseObject.<DealerResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật đại lý thành công")
                        .data(updated)
                        .build()
        );
    }

    @Operation(summary = "Xóa đại lý theo ID")
    @DeleteMapping("/{dealerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<Void>> deleteDealer(@PathVariable Long dealerId) {
        dealerService.deleteDealer(dealerId);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Xóa đại lý thành công")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Lấy thông tin chi tiết đại lý theo ID")
    @GetMapping("/{dealerId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<DealerResponse>> getDealerById(@PathVariable Long dealerId) {
        DealerResponse dealer = dealerService.getDealerById(dealerId);

        return ResponseEntity.ok(
                ResponseObject.<DealerResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy thông tin đại lý thành công")
                        .data(dealer)
                        .build()
        );
    }

    @Operation(summary = "Lấy danh sách tất cả đại lý")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<List<DealerResponse>>> getAllDealers() {

        List<DealerResponse> list = dealerService.getAllDealers();

        return ResponseEntity.ok(
                ResponseObject.<List<DealerResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy danh sách đại lý thành công")
                        .data(list)
                        .build()
        );
    }

}
