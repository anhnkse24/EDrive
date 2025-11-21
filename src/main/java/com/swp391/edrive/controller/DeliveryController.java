package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.DeliveryResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.DeliveryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/orders/{orderId}/confirm-delivery")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<DeliveryResponse>> confirmDelivery(@PathVariable String orderId) {
        try {
            deliveryService.confirmDelivery(orderId);

            DeliveryResponse data = DeliveryResponse.builder()
                    .orderId(orderId)
                    .status("DELIVERED")
                    .message("Delivery confirmed successfully for order: " + orderId)
                    .confirmedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(
                    ResponseObject.<DeliveryResponse>builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Xác nhận giao hàng thành công")
                            .data(data)
                            .build()
            );

        } catch (IllegalArgumentException e) {

            DeliveryResponse errorData = DeliveryResponse.builder()
                    .orderId(orderId)
                    .status("NOT_FOUND")
                    .message(e.getMessage())
                    .confirmedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.<DeliveryResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Không tìm thấy đơn hàng")
                            .data(errorData)
                            .build()
                    );

        } catch (IllegalStateException e) {

            DeliveryResponse errorData = DeliveryResponse.builder()
                    .orderId(orderId)
                    .status("FAILED")
                    .message(e.getMessage())
                    .confirmedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.badRequest()
                    .body(ResponseObject.<DeliveryResponse>builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Không thể xác nhận giao hàng")
                            .data(errorData)
                            .build()
                    );

        } catch (Exception e) {

            DeliveryResponse errorData = DeliveryResponse.builder()
                    .orderId(orderId)
                    .status("ERROR")
                    .message("Unexpected error: " + e.getMessage())
                    .confirmedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.internalServerError()
                    .body(ResponseObject.<DeliveryResponse>builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi hệ thống khi xác nhận giao hàng")
                            .data(errorData)
                            .build()
                    );
        }
    }
}
