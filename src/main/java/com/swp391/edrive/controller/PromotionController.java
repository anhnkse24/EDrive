package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.PromotionService;
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
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "API quản lý khuyến mãi (CRUD + Dealer)")
@SecurityRequirement(name = "api")

public class PromotionController {

    private final PromotionService promotionService;


    @Operation(summary = "Lấy danh sách tất cả khuyến mãi")
    @GetMapping
    @PreAuthorize("hasAnyRole('DEALER_MANAGER','DEALER_STAFF')")
    public ResponseEntity<ResponseObject<List<PromotionResponse>>> getAll() {

        List<PromotionResponse> list = promotionService.getAllPromotions();

        return ResponseEntity.ok(
                ResponseObject.<List<PromotionResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Danh sách mã khuyến mãi")
                        .data(list)
                        .build()
        );
    }

    @Operation(summary = "Lấy danh sách khuyến mãi theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER','DEALER_STAFF')")
    public ResponseEntity<ResponseObject<List<PromotionResponse>>> getByDealerId(@PathVariable Long dealerId) {

        List<PromotionResponse> list = promotionService.getPromotionsByDealerId(dealerId);

        return ResponseEntity.ok(
                ResponseObject.<List<PromotionResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Danh sách mã khuyến mãi theo đại lí")
                        .data(list)
                        .build()
        );
    }

    @Operation(summary = "Lấy khuyến mãi cụ thể theo ID và Dealer ID")
    @GetMapping("/dealer/{dealerId}/{promotionId}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER','DEALER_STAFF')")
    public ResponseEntity<ResponseObject<PromotionResponse>> getByIdAndDealerId(
            @PathVariable Long dealerId,
            @PathVariable Long promotionId) {

        PromotionResponse res = promotionService.getPromotionByIdAndDealerId(promotionId, dealerId);

        return ResponseEntity.ok(
                ResponseObject.<PromotionResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Danh sách mã khuyến mãi")
                        .data(res)
                        .build()
        );
    }

    @Operation(summary = "Tạo khuyến mãi mới cho Dealer cụ thể")
    @PostMapping("/dealer/{dealerId}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<PromotionResponse>> createByDealer(
            @PathVariable Long dealerId,
            @Valid @RequestBody PromotionRequest req) {

        PromotionResponse res = promotionService.createPromotionByDealer(dealerId, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.<PromotionResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Tạo mã khuyến mãi thành công")
                        .data(res)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật khuyến mãi theo ID và Dealer ID")
    @PutMapping("/dealer/{dealerId}/{promotionId}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<PromotionResponse>> updateByDealer(
            @PathVariable Long dealerId,
            @PathVariable Long promotionId,
            @Valid @RequestBody PromotionRequest req) {

        PromotionResponse updated = promotionService.updatePromotionByDealer(dealerId, promotionId, req);

        return ResponseEntity.ok(
                ResponseObject.<PromotionResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật mã khuyến mãi thành công")
                        .data(updated)
                        .build()
        );
    }

    @Operation(summary = "Xoá khuyến mãi theo ID và Dealer ID")
    @DeleteMapping("/dealer/{dealerId}/{promotionId}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<Void>> deleteByDealer(
            @PathVariable Long dealerId,
            @PathVariable Long promotionId) {

        promotionService.deletePromotionByDealer(dealerId, promotionId);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Xoá mã khuyến mãi thành công")
                        .data(null)
                        .build()
        );
    }
}
