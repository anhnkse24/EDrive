package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.enums.OrderStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {
    OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId);

    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String orderId);
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    List<OrderResponse> getOrdersByDealerId(Long dealerId);
    OrderResponse cancelOrder(String orderId);

    // Bill Management
    String uploadPaymentImage(String orderId, MultipartFile bill);
    byte[] getPaymentBillContent(String orderId) throws IOException;
    String getPaymentBillContentType(String orderId);
    String getPaymentBillFileName(String orderId);
    OrderResponse markOrderAsPaid(String orderId);
}
