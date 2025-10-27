package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CashPaymentRequest;
import com.swp391.edrive.dto.request.VnPayLinkRequest;
import com.swp391.edrive.dto.response.CashPaymentResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.VnPayLinkResponse;

import java.util.Map;

public interface PaymentService {

    // tạo link VNPay cho Order
    ResponseObject createVnPayUrl(String orderId);

    // xử lý VNPay redirect (return URL)
    Map<String, String> handleVnPayReturn(Map<String, String> params);
}
