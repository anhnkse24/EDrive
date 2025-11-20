package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.entity.Contract;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsByDealer(Long dealerId);
    NotificationResponse markAsRead(Long notificationId);
    List<NotificationResponse> getAllNotifications();
    void createAdminNotificationForDealerRequest(Long dealerId);
    void createAdminNotificationForDealerOrder(String orderId);
    void createNotificationForTestDrive(Long dealerId, Long testDriveId);
    void createAdminNotificationForUploadedBill(String orderId);
    List<NotificationResponse> getNotificationsForAdmin();
    void createNotificationForUploadedContract(Contract contract);

}