package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Notification;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.OrderItem;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.NotificationRepository;
import com.swp391.edrive.repository.OrderRepository;
import com.swp391.edrive.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final DealerRepository dealerRepository;
    private final OrderRepository orderRepository;


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

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void createAdminNotificationForDealerRequest(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Dealer not found"));

        Notification notification = Notification.builder()
                .dealer(dealer)
                .title("Yêu cầu đăng ký làm đại lý mới")
                .message("Dealer " + dealer.getDealerName() + " vừa gửi yêu cầu đăng ký làm đại lý.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public void createAdminNotificationForDealerOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Dealer dealer = order.getDealer();

        // ✅ Kiểm tra null tránh lỗi NullPointerException
        int totalQuantity = 0;
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            totalQuantity = order.getOrderItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
        }

        Notification notification = Notification.builder()
                .dealer(dealer)
                .title("Đơn hàng mới từ đại lý")
                .message("Đại lý " + dealer.getDealerName() +
                        " vừa đặt " + totalQuantity + " xe trong đơn " + order.getOrderId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
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
