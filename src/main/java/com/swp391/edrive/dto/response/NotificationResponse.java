package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Long dealerId;
    private String dealerName;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}