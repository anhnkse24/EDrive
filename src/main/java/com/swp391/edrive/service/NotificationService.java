package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.TestDriveStatusRequest;
import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.entity.Contract;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.TestDrive;

import java.util.List;

public interface NotificationService {
    NotificationResponse markAsRead(Long notificationId);
    List<NotificationResponse> getAllNotifications();
    void createAdminNotificationForDealerRequest(Long dealerId);
    void createAdminNotificationForDealerOrder(String orderId);
    void createNotificationForTestDrive(Long dealerId, Long testDriveId);
    void createAdminNotificationForUploadedBill(String orderId);
    List<NotificationResponse> getNotificationsForAdmin();
    void createNotificationForUploadedContract(Contract contract);
    void createNotificationForDeliveryConfirmed(Order order);
    List<NotificationResponse> getNotificationsForDealerManager(Long dealerId);
    List<NotificationResponse> getNotificationsForDealerStaff(Long dealerId);
    void createNotificationForTestDriveStatusForStaff(TestDrive testDrive, TestDriveStatusRequest request);
}