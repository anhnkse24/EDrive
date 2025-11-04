package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.entity.Notification;
import com.swp391.edrive.repository.NotificationRepository;
import com.swp391.edrive.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> getNotificationsByDealer(Long dealerId) {
        List<Notification> notifications = notificationRepository.findByDealer_DealerIdOrderByCreatedAtDesc(dealerId);
        if (notifications.isEmpty())
            throw new EntityNotFoundException("Không có thông báo nào cho đại lý ID: " + dealerId);

        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .dealerId(notification.getDealer().getDealerId())
                .dealerName(notification.getDealer().getDealerName())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
