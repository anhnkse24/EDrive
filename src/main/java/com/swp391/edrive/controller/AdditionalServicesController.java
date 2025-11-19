package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ServiceCatalogRequest;
import com.swp391.edrive.dto.response.ServiceCatalogResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.AdditionalServicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/additional-services")
@SecurityRequirement(name = "api")
public class AdditionalServicesController {

    private final AdditionalServicesService additionalServicesService;

    @Operation(summary = "Lấy tất cả dịch vụ đang hoạt động")
    @GetMapping("/active")
    public ResponseEntity<ResponseObject<List<ServiceCatalogResponse>>> getAllActiveServices() {
        try {
            List<ServiceCatalogResponse> services = additionalServicesService.getAllActiveServices();
            ResponseObject<List<ServiceCatalogResponse>> response = ResponseObject.<List<ServiceCatalogResponse>>builder()
                    .statusCode(200)
                    .message("Lấy danh sách dịch vụ thành công")
                    .data(services)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<List<ServiceCatalogResponse>> response = ResponseObject.<List<ServiceCatalogResponse>>builder()
                    .statusCode(500)
                    .message("Lỗi khi lấy danh sách dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Lấy dịch vụ theo ID")
    @GetMapping("/{serviceId}")
    public ResponseEntity<ResponseObject<ServiceCatalogResponse>> getServiceById(@PathVariable Long serviceId) {
        try {
            ServiceCatalogResponse service = additionalServicesService.getServiceById(serviceId);
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(200)
                    .message("Lấy dịch vụ thành công")
                    .data(service)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(404)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(500)
                    .message("Lỗi khi lấy dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Lấy dịch vụ theo category")
    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseObject<List<ServiceCatalogResponse>>> getServicesByCategory(@PathVariable String category) {
        try {
            List<ServiceCatalogResponse> services = additionalServicesService.getServicesByCategory(category);
            ResponseObject<List<ServiceCatalogResponse>> response = ResponseObject.<List<ServiceCatalogResponse>>builder()
                    .statusCode(200)
                    .message("Lấy danh sách dịch vụ theo category thành công")
                    .data(services)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<List<ServiceCatalogResponse>> response = ResponseObject.<List<ServiceCatalogResponse>>builder()
                    .statusCode(500)
                    .message("Lỗi khi lấy danh sách dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Tìm kiếm dịch vụ")
    @GetMapping("/search")
    public ResponseEntity<ResponseObject<Page<ServiceCatalogResponse>>> searchServices(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ServiceCatalogResponse> services = additionalServicesService.searchServices(keyword, pageable);
            ResponseObject<Page<ServiceCatalogResponse>> response = ResponseObject.<Page<ServiceCatalogResponse>>builder()
                    .statusCode(200)
                    .message("Tìm kiếm dịch vụ thành công")
                    .data(services)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<Page<ServiceCatalogResponse>> response = ResponseObject.<Page<ServiceCatalogResponse>>builder()
                    .statusCode(500)
                    .message("Lỗi khi tìm kiếm dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Lấy tất cả dịch vụ (bao gồm inactive - cho admin)")
    @GetMapping("/all")
    public ResponseEntity<ResponseObject<Page<ServiceCatalogResponse>>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ServiceCatalogResponse> services = additionalServicesService.getAllServices(pageable);
            ResponseObject<Page<ServiceCatalogResponse>> response = ResponseObject.<Page<ServiceCatalogResponse>>builder()
                    .statusCode(200)
                    .message("Lấy tất cả dịch vụ thành công")
                    .data(services)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<Page<ServiceCatalogResponse>> response = ResponseObject.<Page<ServiceCatalogResponse>>builder()
                    .statusCode(500)
                    .message("Lỗi khi lấy danh sách dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Tạo dịch vụ mới")
    @PostMapping
    public ResponseEntity<ResponseObject<ServiceCatalogResponse>> createService(@RequestBody @Valid ServiceCatalogRequest request) {
        try {
            ServiceCatalogResponse service = additionalServicesService.createService(request);
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(201)
                    .message("Tạo dịch vụ thành công")
                    .data(service)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(500)
                    .message("Lỗi khi tạo dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Cập nhật dịch vụ")
    @PutMapping("/{serviceId}")
    public ResponseEntity<ResponseObject<ServiceCatalogResponse>> updateService(
            @PathVariable Long serviceId,
            @RequestBody @Valid ServiceCatalogRequest request) {
        try {
            ServiceCatalogResponse service = additionalServicesService.updateService(serviceId, request);
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(200)
                    .message("Cập nhật dịch vụ thành công")
                    .data(service)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(404)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ResponseObject<ServiceCatalogResponse> response = ResponseObject.<ServiceCatalogResponse>builder()
                    .statusCode(500)
                    .message("Lỗi khi cập nhật dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Vô hiệu hóa dịch vụ (xóa mềm)")
    @PatchMapping("/{serviceId}/deactivate")
    public ResponseEntity<ResponseObject<Void>> deactivateService(@PathVariable Long serviceId) {
        try {
            additionalServicesService.deactivateService(serviceId);
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(200)
                    .message("Vô hiệu hóa dịch vụ thành công")
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(404)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(500)
                    .message("Lỗi khi vô hiệu hóa dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Kích hoạt lại dịch vụ")
    @PatchMapping("/{serviceId}/activate")
    public ResponseEntity<ResponseObject<Void>> activateService(@PathVariable Long serviceId) {
        try {
            additionalServicesService.activateService(serviceId);
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(200)
                    .message("Kích hoạt dịch vụ thành công")
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(404)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(500)
                    .message("Lỗi khi kích hoạt dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Xóa dịch vụ vĩnh viễn")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ResponseObject<Void>> deleteService(@PathVariable Long serviceId) {
        try {
            additionalServicesService.deleteService(serviceId);
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(200)
                    .message("Xóa dịch vụ thành công")
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(404)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ResponseObject<Void> response = ResponseObject.<Void>builder()
                    .statusCode(500)
                    .message("Lỗi khi xóa dịch vụ: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

