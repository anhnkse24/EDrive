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
    public ResponseEntity<ResponseObject> getAll() {
        List<PromotionResponse> list = promotionService.getAllPromotions();
        return ResponseEntity.ok(new ResponseObject(200, "Promotions retrieved successfully", list));
    }


    @Operation(summary = "Lấy danh sách khuyến mãi theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> getByDealerId(@PathVariable Long dealerId) {
        List<PromotionResponse> list = promotionService.getPromotionsByDealerId(dealerId);
        return ResponseEntity.ok(new ResponseObject(200,
                "Promotions retrieved successfully by dealer ID", list));
    }

    @Operation(summary = "Lấy khuyến mãi cụ thể theo ID và Dealer ID")
    @GetMapping("/dealer/{dealerId}/{promotionId}")
    public ResponseEntity<ResponseObject> getByIdAndDealerId(@PathVariable Long dealerId,
                                                             @PathVariable Long promotionId) {
        PromotionResponse res = promotionService.getPromotionByIdAndDealerId(promotionId, dealerId);
        return ResponseEntity.ok(new ResponseObject(200,
                "Promotion retrieved successfully by dealer ID", res));
    }

    @Operation(summary = "Tạo khuyến mãi mới cho Dealer cụ thể")
    @PostMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> createByDealer(@PathVariable Long dealerId,
                                                         @Valid @RequestBody PromotionRequest req) {
        PromotionResponse res = promotionService.createPromotionByDealer(dealerId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201,
                        "Promotion created successfully for dealer", res));
    }

    @Operation(summary = "Cập nhật khuyến mãi theo ID và Dealer ID")
    @PutMapping("/dealer/{dealerId}/{promotionId}")
    public ResponseEntity<ResponseObject> updateByDealer(@PathVariable Long dealerId,
                                                         @PathVariable Long promotionId,
                                                         @Valid @RequestBody PromotionRequest req) {
        PromotionResponse updated = promotionService.updatePromotionByDealer(dealerId, promotionId, req);
        return ResponseEntity.ok(new ResponseObject(200,
                "Promotion updated successfully for dealer", updated));
    }

    @Operation(summary = "Xoá khuyến mãi theo ID và Dealer ID")
    @DeleteMapping("/dealer/{dealerId}/{promotionId}")
    public ResponseEntity<ResponseObject> deleteByDealer(@PathVariable Long dealerId,
                                                         @PathVariable Long promotionId) {
        promotionService.deletePromotionByDealer(dealerId, promotionId);
        return ResponseEntity.ok(new ResponseObject(200,
                "Promotion deleted successfully for dealer", null));
    }
}
