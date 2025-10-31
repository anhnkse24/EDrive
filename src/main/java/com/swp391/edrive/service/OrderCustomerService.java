package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.OrderCustomerRequest;
import com.swp391.edrive.dto.request.StatusOrderCustomerRequest;
import com.swp391.edrive.dto.response.OrderCustomerResponse;
import com.swp391.edrive.entity.OrderCustomer;
import com.swp391.edrive.entity.StatusOrderCustomer;

import java.util.List;
import java.util.Optional;

public interface OrderCustomerService {

    // 🔹 Lấy tất cả đơn hàng
    List<OrderCustomerResponse> getAllOrders();

    // 🔹 Lấy đơn hàng theo Customer ID
    List<OrderCustomerResponse> getOrdersByCustomerId(Long customerId);

    // 🔹 Lấy đơn hàng theo ID
    Optional<OrderCustomerResponse> getOrderById(Long id);

    // 🔹 Tạo đơn hàng mới
    OrderCustomerResponse createOrder(OrderCustomerRequest request);

    // 🔹 Cập nhật thông tin đơn hàng
    OrderCustomerResponse updateOrder(Long id, OrderCustomerRequest req);

    // 🔹 Xóa đơn hàng
    void deleteOrder(Long id);

    // 🔹 Cập nhật trạng thái đơn hàng
    StatusOrderCustomer updateOrderStatus(Long orderId, StatusOrderCustomerRequest request);
}
