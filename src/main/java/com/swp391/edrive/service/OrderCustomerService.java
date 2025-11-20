//package com.swp391.edrive.service;
//
//import com.swp391.edrive.dto.request.OrderCustomerRequest;
//import com.swp391.edrive.dto.request.StatusOrderCustomerRequest;
//import com.swp391.edrive.dto.response.OrderCustomerResponse;
//import com.swp391.edrive.entity.StatusOrderCustomer;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface OrderCustomerService {
//
//    List<OrderCustomerResponse> getAllOrders();
//
//    List<OrderCustomerResponse> getOrdersByCustomerId(Long customerId);
//
//    Optional<OrderCustomerResponse> getOrderById(Long id);
//
//    OrderCustomerResponse createOrder(OrderCustomerRequest request);
//
//    OrderCustomerResponse updateOrder(Long id, OrderCustomerRequest req);
//
//    void deleteOrder(Long id);
//
//    StatusOrderCustomer updateOrderStatus(Long orderId, StatusOrderCustomerRequest request);
//}
