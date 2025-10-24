package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Đại lý tạo Order + thu tiền mặt FULL ngay
    @PostMapping("/cash")
    public OrderSummaryResponse createByDealerCash(@RequestBody OrderCreateRequest req) {
        Long dealerId = getDealerIdFromAuth(); // TODO: lấy từ SecurityContext (user role DEALER)
        return orderService.createOrderByDealerCashOnly(req, dealerId);
    }

    private Long getDealerIdFromAuth() {
        // TODO: lấy từ JWT/SecurityContext -> user.getDealer().getDealerId()
        return 1L; // demo
    }
}
