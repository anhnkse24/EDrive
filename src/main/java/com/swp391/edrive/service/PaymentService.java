package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CashPaymentRequest;
import com.swp391.edrive.dto.request.VnPayLinkRequest;
import com.swp391.edrive.dto.response.CashPaymentResponse;
import com.swp391.edrive.dto.response.VnPayLinkResponse;

import java.util.Map;

public interface PaymentService {
    CashPaymentResponse payCash(CashPaymentRequest req);

    // tạo link VNPay cho Order
    VnPayLinkResponse createVnPayUrl(Long orderId);

    // xử lý VNPay redirect (return URL)
    Map<String, String> handleVnPayReturn(Map<String, String> params);
}
