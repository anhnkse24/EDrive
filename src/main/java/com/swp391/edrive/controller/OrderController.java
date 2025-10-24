package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderGetResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 1) Tạo Order (chưa thanh toán)
    @PostMapping
    public OrderSummaryResponse create(@RequestBody OrderCreateRequest req) {
        Long dealerId = getDealerIdFromAuth(); // TODO: lấy từ SecurityContext/JWT
        return orderService.createOrder(req, dealerId);
    }

    private Long getDealerIdFromAuth() { return 1L; } // demo
}
