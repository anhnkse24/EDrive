package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        return orderService.createOrder(req, dealerId, user);
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
                .message("Lấy thông tin đơn hàng thành công")
                .data(result)
                .build();
    }

    @GetMapping("/status/{status}")
    public ResponseObject<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> result = orderService.getOrdersByStatus(status);
        return ResponseObject.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách đơn hàng theo trạng thái thành công")
                .data(result)
                .build();
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseObject<List<OrderResponse>> getOrdersByDealerId(@PathVariable Long dealerId) {
        List<OrderResponse> result = orderService.getOrdersByDealerId(dealerId);
        return ResponseObject.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách đơn hàng theo đại lý thành công")
                .data(result)
                .build();
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseObject<OrderResponse> cancel(@PathVariable String orderId) {
        OrderResponse result = orderService.cancelOrder(orderId);
        return ResponseObject.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Hủy đơn hàng thành công")
                .data(result).build();
    }

    @Operation(summary = "Upload payment bill after order payment", description = "Dealer uploads payment bill (invoice) after completing payment for the order")
    @PostMapping(value = "/{orderId}/upload-bill", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseObject<String> uploadPaymentBill(@PathVariable String orderId,
                                                    @RequestParam("bill") MultipartFile bill) {
        String result = orderService.uploadPaymentImage(orderId, bill);
        return ResponseObject.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tải lên hóa đơn thanh toán thành công")
                .data(result)
                .build();
    }

    @Operation(summary = "View payment bill image", description = "View the payment bill as image/PDF in browser")
    @GetMapping("/{orderId}/bill-preview")
    public ResponseEntity<?> viewBill(@PathVariable String orderId) {
        try {
            byte[] fileContent = orderService.getPaymentBillContent(orderId);
            String contentType = orderService.getPaymentBillContentType(orderId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Không tìm thấy hóa đơn: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Download payment bill", description = "Download the payment bill file to local machine")
    @GetMapping("/{orderId}/download-bill")
    public ResponseEntity<?> downloadBill(@PathVariable String orderId) {
        try {
            byte[] fileContent = orderService.getPaymentBillContent(orderId);
            String fileName = orderService.getPaymentBillFileName(orderId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Không tìm thấy hóa đơn: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Admin confirm payment and mark order as PAID", description = "Admin endpoint to mark order payment status as PAID after verifying the uploaded bill")
    @PutMapping("/{orderId}/mark-paid")
    public ResponseObject<OrderResponse> markOrderAsPaid(@PathVariable String orderId) {
        OrderResponse result = orderService.markOrderAsPaid(orderId);
        return ResponseObject.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xác nhận thanh toán thành công")
                .data(result)
                .build();
    }
}
