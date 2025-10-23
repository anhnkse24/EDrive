package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CreateCashPaymentRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Thu tiền mặt (DEPOSIT/INSTALLMENT/FULL) cho Order")
    @PostMapping("/cash")
    public ResponseEntity<ResponseObject> cash(@RequestBody CreateCashPaymentRequest req) {
        try {
            var data = paymentService.createCashPayment(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(201, "Cash payment created", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> listByOrder(@PathVariable Long orderId) {
        var data = paymentService.listByOrder(orderId);
        return ResponseEntity.ok(new ResponseObject(200, "Payments by order", data));
    }
}
