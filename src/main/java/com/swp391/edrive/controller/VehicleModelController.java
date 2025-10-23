package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.VehicleModelUpsertRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.VehicleModelResponse;
import com.swp391.edrive.service.VehicleModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@Tag(name = "Vehicle Models", description = "API quản lý dòng xe (Model)")
@RequiredArgsConstructor
public class VehicleModelController {
    private final VehicleModelService modelService;

    @Operation(summary = "Tạo model")
    @PostMapping
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody VehicleModelUpsertRequest req) {
        VehicleModelResponse data = modelService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Vehicle model created", data));
    }

    @Operation(summary = "Lấy 1 model theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        VehicleModelResponse data = modelService.get(id);
        return ResponseEntity.ok(new ResponseObject(200, "Vehicle model found", data));
    }

    @Operation(summary = "Danh sách model")
    @GetMapping
    public ResponseEntity<ResponseObject> list() {
        List<VehicleModelResponse> data = modelService.list();
        return ResponseEntity.ok(new ResponseObject(200, "Vehicle models retrieved", data));
    }

    @Operation(summary = "Cập nhật model")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id,
                                                 @Valid @RequestBody VehicleModelUpsertRequest req) {
        var data = modelService.update(id, req);
        return ResponseEntity.ok(new ResponseObject(200, "Vehicle model updated", data));
    }

    @Operation(summary = "Xóa model (chỉ khi không còn version)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseEntity.ok(new ResponseObject(200, "Vehicle model deleted", null));
    }
}
