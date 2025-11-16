package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.DeliveryResponse;
import com.swp391.edrive.service.DeliveryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/orders/{orderId}/confirm-delivery")
    public ResponseEntity<?> confirmDelivery(@PathVariable String orderId) {
        try {
            deliveryService.confirmDelivery(orderId);

            DeliveryResponse response = DeliveryResponse.builder()
                    .orderId(orderId)
                    .status("DELIVERED")
                    .message("Delivery confirmed successfully for order: " + orderId)
                    .confirmedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    DeliveryResponse.builder()
                            .orderId(orderId)
                            .status("NOT_FOUND")
                            .message(e.getMessage())
                            .confirmedAt(LocalDateTime.now())
                            .build()
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    DeliveryResponse.builder()
                            .orderId(orderId)
                            .status("FAILED")
                            .message(e.getMessage())
                            .confirmedAt(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    DeliveryResponse.builder()
                            .orderId(orderId)
                            .status("ERROR")
                            .message("Unexpected error: " + e.getMessage())
                            .confirmedAt(LocalDateTime.now())
                            .build()
            );
        }
    }
}
