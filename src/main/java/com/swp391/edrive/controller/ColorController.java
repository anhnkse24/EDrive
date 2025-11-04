package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ColorRequest;
import com.swp391.edrive.dto.response.ColorResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.ColorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/colors")
@SecurityRequirement(name = "api")
public class ColorController {

    private final ColorService colorService;

    @Operation(summary = "Tạo màu mới")
    @PostMapping
    public ResponseEntity<ResponseObject<ColorResponse>> create(@RequestBody @Valid ColorRequest req) {
        try {
            ColorResponse colorResponse = colorService.create(req);
            ResponseObject<ColorResponse> response = ResponseObject.<ColorResponse>builder()
                    .statusCode(200)
                    .message("Color created successfully")
                    .data(colorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<ColorResponse> response = ResponseObject.<ColorResponse>builder()
                    .statusCode(500)
                    .message("Error creating color")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Cập nhật màu")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<ColorResponse>> update(@PathVariable Long id, @RequestBody @Valid ColorRequest req) {
        try {
            ColorResponse colorResponse = colorService.update(id, req);
            ResponseObject<ColorResponse> response = ResponseObject.<ColorResponse>builder()
                    .statusCode(200)
                    .message("Color updated successfully")
                    .data(colorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<ColorResponse> response = ResponseObject.<ColorResponse>builder()
                    .statusCode(500)
                    .message("Error updating color")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Xoá màu")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> delete(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        try {
            colorService.delete(id, force);
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(200)
                    .message("Color deleted successfully")
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(500)
                    .message("Error deleting color")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Lấy màu theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<ColorResponse>> getById(@PathVariable Long id) {
        try {
            ColorResponse colorResponse = colorService.getById(id);
            ResponseObject<ColorResponse> response = ResponseObject.<ColorResponse>builder()
                    .statusCode(200)
                    .message("Color fetched successfully")
                    .data(colorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<ColorResponse> response = ResponseObject.<ColorResponse>builder()
                    .statusCode(500)
                    .message("Error fetching color")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Danh sách màu")
    @GetMapping
    public ResponseEntity<ResponseObject<List<ColorResponse>>> list(@RequestParam(required = false) String q) {
        try {
            List<ColorResponse> colors = (q == null || q.isBlank()) ? colorService.getAll() : colorService.search(q);
            ResponseObject<List<ColorResponse>> response = ResponseObject.<List<ColorResponse>>builder()
                    .statusCode(200)
                    .message("Color list retrieved")
                    .data(colors)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<List<ColorResponse>> response = ResponseObject.<List<ColorResponse>>builder()
                    .statusCode(500)
                    .message("Error fetching color list")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
