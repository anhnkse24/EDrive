package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderSummaryResponse;

public interface OrderService {
    OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId);
}
