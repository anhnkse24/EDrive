package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CreateCashPaymentRequest;
import com.swp391.edrive.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createCashPayment(CreateCashPaymentRequest req);
    List<PaymentResponse> listByOrder(Long orderId);
}
