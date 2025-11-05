package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsByDealer(Long dealerId);
    NotificationResponse markAsRead(Long notificationId);
    List<NotificationResponse> getAllNotifications();
    void createAdminNotificationForDealerRequest(Long dealerId);
    void createAdminNotificationForDealerOrder(String orderId);

}
