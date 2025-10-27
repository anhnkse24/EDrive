package com.swp391.edrive.controller;

import com.swp391.edrive.service.DeliveryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/orders/{orderId}/confirm-delivery")
    public String confirmDelivery(@PathVariable String orderId) {
        deliveryService.confirmDelivery(orderId);
        return "Delivery confirmed successfully for order: " + orderId;
    }
}