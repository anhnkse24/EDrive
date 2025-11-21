package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "Notification", description = "Quản lý thông báo của Dealer và Admin")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo Dealer Manager")
    @GetMapping("/dealer/manager/{dealerId}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER')")
    public List<NotificationResponse> getNotificationsByDealerManager(@PathVariable Long dealerId) {
        return notificationService.getNotificationsForDealerManager(dealerId);
    }
    @Operation(summary = "Lấy danh sách thông báo Dealer Manager")
    @GetMapping("/dealer/staff/{dealerId}")
    @PreAuthorize("hasAnyRole('DEALER_STAFF')")
    public List<NotificationResponse> getNotificationsByDealerStaff(@PathVariable Long dealerId) {
        return notificationService.getNotificationsForDealerStaff(dealerId);
    }

    @Operation(summary = "Lấy danh sách thông báo dành cho Admin")
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<NotificationResponse> getNotificationsForAdmin() {
        return notificationService.getNotificationsForAdmin();
    }

    @Operation(summary = "Lấy tất cả thông báo")
    @GetMapping("/all")
    public List<NotificationResponse> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // Đánh dấu đã đọc
    @Operation(summary = "Đánh dấu đã đọc")
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public NotificationResponse markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }
}