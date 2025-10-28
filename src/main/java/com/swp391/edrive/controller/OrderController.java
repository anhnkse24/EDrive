package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class OrderController {
    private final OrderService orderService;

    // 1) Tạo Order (chưa thanh toán)
    @PostMapping
    public OrderSummaryResponse create(@RequestBody OrderCreateRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long dealerId = user.getDealer().getDealerId();
        return orderService.createOrder(req, dealerId);
    }
    @GetMapping
    public ResponseObject<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> result = orderService.getAllOrders();
        return ResponseObject.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Fetched all orders successfully")
                .data(result)
                .build();
    }

    @GetMapping("/{orderId}")
    public ResponseObject<OrderResponse> getOrderById(@PathVariable String orderId) {
        OrderResponse result = orderService.getOrderById(orderId);
        return ResponseObject.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Fetched order successfully")
                .data(result)
                .build();
    }

    @GetMapping("/status/{status}")
    public ResponseObject<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> result = orderService.getOrdersByStatus(status);
        return ResponseObject.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Fetched orders by status successfully")
                .data(result)
                .build();
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseObject<List<OrderResponse>> getOrdersByDealerId(@PathVariable Long dealerId) {
        List<OrderResponse> result = orderService.getOrdersByDealerId(dealerId);
        return ResponseObject.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Fetched orders by dealerId successfully")
                .data(result)
                .build();
    }

}
