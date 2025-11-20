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

        ColorResponse colorResponse = colorService.create(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.<ColorResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Color created successfully")
                        .data(colorResponse)
                        .build()
                );
    }

    @Operation(summary = "Cập nhật màu")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<ColorResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid ColorRequest req) {

        ColorResponse colorResponse = colorService.update(id, req);

        return ResponseEntity.ok(
                ResponseObject.<ColorResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Color updated successfully")
                        .data(colorResponse)
                        .build()
        );
    }

    @Operation(summary = "Xoá màu")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force) {

        colorService.delete(id, force);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Color deleted successfully")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Lấy màu theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<ColorResponse>> getById(@PathVariable Long id) {

        ColorResponse colorResponse = colorService.getById(id);

        return ResponseEntity.ok(
                ResponseObject.<ColorResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Color fetched successfully")
                        .data(colorResponse)
                        .build()
        );
    }

    @Operation(summary = "Danh sách màu")
    @GetMapping
    public ResponseEntity<ResponseObject<List<ColorResponse>>> list() {

        List<ColorResponse> colors = colorService.getAll();

        return ResponseEntity.ok(
                ResponseObject.<List<ColorResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Color list retrieved")
                        .data(colors)
                        .build()
        );
    }


}
