package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CashPaymentRequest;
import com.swp391.edrive.dto.request.VnPayLinkRequest;
import com.swp391.edrive.dto.response.CashPaymentResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.VnPayLinkResponse;
import com.swp391.edrive.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class PaymentController {
    private final PaymentService paymentService;


    // VNPay: tạo link theo orderId (match style: /vnpay/{id})
    @PostMapping("/vnpay/{orderId}")
    public ResponseObject createVnPayUrl(@PathVariable String orderId) {
        return paymentService.createVnPayUrl(orderId);
    }

    // VNPay: return URL (redirect từ VNPay)
    @GetMapping("/vnpay-return")
    public Map<String, String> handleVnPayReturn(@RequestParam Map<String, String> params) {
        return paymentService.handleVnPayReturn(params);
    }



}
