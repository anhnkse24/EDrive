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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<List<ServiceCatalogResponse>>> getAllActiveServices() {

        List<ServiceCatalogResponse> services = additionalServicesService.getAllActiveServices();

        return ResponseEntity.ok(
                ResponseObject.<List<ServiceCatalogResponse>>builder()
                        .statusCode(200)
                        .message("Lấy danh sách dịch vụ thành công")
                        .data(services)
                        .build()
        );
    }

    @Operation(summary = "Lấy dịch vụ theo ID")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    @GetMapping("/{serviceId}")
    public ResponseEntity<ResponseObject<ServiceCatalogResponse>> getServiceById(@PathVariable Long serviceId) {

        ServiceCatalogResponse service = additionalServicesService.getServiceById(serviceId);

        return ResponseEntity.ok(
                ResponseObject.<ServiceCatalogResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy dịch vụ thành công")
                        .data(service)
                        .build()
        );
    }

    @Operation(summary = "Lấy dịch vụ theo category")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseObject<List<ServiceCatalogResponse>>> getServicesByCategory(
            @PathVariable String category) {

        List<ServiceCatalogResponse> services = additionalServicesService.getServicesByCategory(category);

        return ResponseEntity.ok(
                ResponseObject.<List<ServiceCatalogResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy danh sách dịch vụ theo category thành công")
                        .data(services)
                        .build()
        );
    }

    @Operation(summary = "Tìm kiếm dịch vụ")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<ResponseObject<Page<ServiceCatalogResponse>>> searchServices(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceCatalogResponse> services = additionalServicesService.searchServices(keyword, pageable);

        return ResponseEntity.ok(
                ResponseObject.<Page<ServiceCatalogResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Tìm kiếm dịch vụ thành công")
                        .data(services)
                        .build()
        );
    }

    @Operation(summary = "Lấy tất cả dịch vụ (bao gồm inactive - cho manager)")
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<Page<ServiceCatalogResponse>>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceCatalogResponse> services = additionalServicesService.getAllServices(pageable);

        return ResponseEntity.ok(
                ResponseObject.<Page<ServiceCatalogResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy tất cả dịch vụ thành công")
                        .data(services)
                        .build()
        );
    }


    @Operation(summary = "Tạo dịch vụ mới")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<ServiceCatalogResponse>> createService(
            @RequestBody @Valid ServiceCatalogRequest request) {

        ServiceCatalogResponse service = additionalServicesService.createService(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.<ServiceCatalogResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Tạo dịch vụ thành công")
                        .data(service)
                        .build()
                );
    }


    @Operation(summary = "Cập nhật dịch vụ")
    @PutMapping("/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<ServiceCatalogResponse>> updateService(
            @PathVariable Long serviceId,
            @RequestBody @Valid ServiceCatalogRequest request) {

        ServiceCatalogResponse service = additionalServicesService.updateService(serviceId, request);

        return ResponseEntity.ok(
                ResponseObject.<ServiceCatalogResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật dịch vụ thành công")
                        .data(service)
                        .build()
        );
    }


    @Operation(summary = "Vô hiệu hóa dịch vụ (xóa mềm)")
    @PatchMapping("/{serviceId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<Void>> deactivateService(@PathVariable Long serviceId) {

        additionalServicesService.deactivateService(serviceId);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Vô hiệu hóa dịch vụ thành công")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Kích hoạt lại dịch vụ")
    @PatchMapping("/{serviceId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<Void>> activateService(@PathVariable Long serviceId) {

        additionalServicesService.activateService(serviceId);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Kích hoạt dịch vụ thành công")
                        .data(null)
                        .build()
        );
    }


    @Operation(summary = "Xóa dịch vụ vĩnh viễn")
    @PreAuthorize("hasAnyRole('ADMIN')")
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

