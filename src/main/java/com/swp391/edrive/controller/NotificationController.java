package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Quản lý thông báo của Dealer sau khi customer đặt lịch lái thử")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    public List<NotificationResponse> getNotificationsByDealer(@PathVariable Long dealerId) {
        return notificationService.getNotificationsByDealer(dealerId);
    }

    @Operation(summary = "Đánh dấu đã đọc")
    @PutMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }
}
