package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

}
