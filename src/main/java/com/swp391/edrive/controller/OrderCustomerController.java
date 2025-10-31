package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.OrderCustomerRequest;
import com.swp391.edrive.dto.request.StatusOrderCustomerRequest;
import com.swp391.edrive.dto.response.OrderCustomerResponse;
import com.swp391.edrive.entity.StatusOrderCustomer;
import com.swp391.edrive.service.OrderCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer-orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "api") // 🔒 nếu bạn dùng JWT / OpenAPI
public class OrderCustomerController {

    private final OrderCustomerService orderCustomerService;

    // 🔹 Lấy tất cả đơn hàng
    @Operation(summary = "Lấy danh sách tất cả đơn hàng của khách hàng")
    @GetMapping
    public ResponseEntity<List<OrderCustomerResponse>> getAllOrders() {
        List<OrderCustomerResponse> responses = orderCustomerService.getAllOrders();
        return ResponseEntity.ok(responses);
    }

    // 🔹 Lấy đơn hàng theo ID
    @Operation(summary = "Lấy chi tiết đơn hàng theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderCustomerResponse> getOrderById(@PathVariable Long id) {
        return orderCustomerService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Lấy danh sách đơn hàng theo Customer ID
    @Operation(summary = "Lấy danh sách đơn hàng của 1 khách hàng cụ thể")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderCustomerResponse>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderCustomerResponse> responses = orderCustomerService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    // 🔹 Tạo mới đơn hàng
    @Operation(summary = "Tạo đơn hàng mới cho khách hàng")
    @PostMapping
    public ResponseEntity<OrderCustomerResponse> createOrder(@Valid @RequestBody OrderCustomerRequest request) {
        OrderCustomerResponse response = orderCustomerService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    // 🔹 Cập nhật đơn hàng
    @Operation(summary = "Cập nhật thông tin đơn hàng")
    @PutMapping("/{id}")
    public ResponseEntity<OrderCustomerResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderCustomerRequest request) {
        return ResponseEntity.ok(orderCustomerService.updateOrder(id, request));
    }

    // 🔹 Xóa đơn hàng
    @Operation(summary = "Xóa đơn hàng theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderCustomerService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Cập nhật trạng thái đơn hàng
    @Operation(summary = "Cập nhật trạng thái đơn hàng")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<StatusOrderCustomer> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody StatusOrderCustomerRequest request) {

        StatusOrderCustomer updated = orderCustomerService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(updated);
    }
}
