package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "Notification", description = "Quản lý thông báo của Dealer và Admin")
public class NotificationController {

    private final NotificationService notificationService;

    // 1. Lấy thông báo của 1 dealer
    @Operation(summary = "Lấy danh sách thông báo theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    public List<NotificationResponse> getNotificationsByDealer(@PathVariable Long dealerId) {
        return notificationService.getNotificationsByDealer(dealerId);
    }

    // 2. Lấy thông báo dành cho admin
    @Operation(summary = "Lấy danh sách thông báo dành cho Admin")
    @GetMapping("/admin")
    public List<NotificationResponse> getNotificationsForAdmin() {
        return notificationService.getNotificationsForAdmin(); // bạn cần tạo method này trong service
    }

    // 3. Lấy tất cả thông báo (admin + dealer)
    @Operation(summary = "Lấy tất cả thông báo")
    @GetMapping("/all")
    public List<NotificationResponse> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // Đánh dấu đã đọc
    @Operation(summary = "Đánh dấu đã đọc")
    @PutMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }
}