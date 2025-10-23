package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CreateOrderFromContractRequest;
import com.swp391.edrive.dto.request.UpdateOrderStatusRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Tạo Order từ Contract ACTIVE")
    @PostMapping("/from-contract")
    public ResponseEntity<ResponseObject> create(@RequestBody CreateOrderFromContractRequest req) {
        try {
            var data = orderService.createFromContract(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(201, "Order created from contract", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        try {
            var data = orderService.get(id);
            return ResponseEntity.ok(new ResponseObject(200, "Order found", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseObject> list() {
        var data = orderService.list();
        return ResponseEntity.ok(new ResponseObject(200, "Order list", data));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseObject> updateStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest req) {
        try {
            var data = orderService.updateStatus(id, req);
            return ResponseEntity.ok(new ResponseObject(200, "Order status updated", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }
}
