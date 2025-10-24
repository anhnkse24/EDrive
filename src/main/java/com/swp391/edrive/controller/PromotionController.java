package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "API quản lý khuyến mãi")
public class PromotionController {

    private final PromotionService promotionService;

    @Operation(summary = "Tạo khuyến mãi mới")
    @PostMapping
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody PromotionRequest req) {
        PromotionResponse res = promotionService.createPromotion(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Promotion created successfully", res));
    }

    @Operation(summary = "Lấy danh sách tất cả khuyến mãi")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return ResponseEntity.ok(
                new ResponseObject(200, "Promotions retrieved successfully",
                        promotionService.getAllPromotions())
        );
    }

    @Operation(summary = "Lấy khuyến mãi theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        PromotionResponse res = promotionService.getPromotionById(id);
        return ResponseEntity.ok(new ResponseObject(200, "Promotion retrieved successfully", res));
    }
    @Operation(summary = "Cập nhật thông tin khuyến mãi theo ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest req) {

        PromotionResponse updated = promotionService.updatePromotion(id, req);
        return ResponseEntity.ok(
                new ResponseObject(200, "Promotion updated successfully", updated)
        );
    }

    @Operation(summary = "Xoá khuyến mãi theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ResponseObject(204, "Promotion deleted successfully", null));
    }
}
