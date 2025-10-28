package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId);

    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String orderId);
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    List<OrderResponse> getOrdersByDealerId(Long dealerId);
    OrderResponse cancelOrder(String orderId);

}
