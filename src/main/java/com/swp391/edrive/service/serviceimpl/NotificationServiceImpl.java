package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.entity.*;
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
    public void createNotificationForTestDrive(Long dealerId, Long testDriveId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new EntityNotFoundException("Dealer not found"));

        Notification notification = Notification.builder()
                .dealer(dealer)
                .title("Lịch lái thử mới")
                .receiverType("DEALER")
                .message("Bạn có một lịch lái thử mới với mã #" + testDriveId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public List<NotificationResponse> getNotificationsByDealer(Long dealerId) {
        List<Notification> notifications = notificationRepository
                .findByDealerDealerIdAndReceiverTypeOrderByCreatedAtDesc(dealerId, "DEALER");

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
                .receiverType("ADMIN")
                .title("Yêu cầu xét duyệt đại lý")
                .message("Dealer " + dealer.getDealerName() + " đã gửi yêu cầu đăng ký.")
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

        int totalQuantity = order.getOrderItems() == null ? 0 :
                order.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum();

        Notification notification = Notification.builder()
                .dealer(dealer)
                .receiverType("ADMIN")
                .title("Đơn hàng mới")
                .message("Bạn có đơn hàng mới gồm " + totalQuantity + " xe. Mã đơn #" + order.getOrderId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public void createAdminNotificationForUploadedBill(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

        Notification notification = Notification.builder()
                .dealer(order.getDealer())
                .title("Hóa đơn mới từ đại lý")
                .receiverType("ADMIN")
                .message("Đại lý " + order.getDealer().getDealerName() +
                        " vừa tải lên hóa đơn cho đơn hàng #" + order.getOrderId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public List<NotificationResponse> getNotificationsForAdmin() {
        List<Notification> notifications = notificationRepository.findByReceiverTypeOrderByCreatedAtDesc("ADMIN");
        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void createNotificationForUploadedContract(Contract contract) {
        Dealer dealer = contract.getDealer();
        if (dealer == null) return;

        Notification notification = Notification.builder()
                .dealer(dealer)
                .title("Hợp đồng mới từ nhà sản xuất")
                .receiverType("DEALER")
                .message("Hợp đồng cho đơn hàng #" + contract.getOrder().getOrderId() + " đã được upload.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .dealerId(notification.getDealer() != null ? notification.getDealer().getDealerId() : null)
                .dealerName(notification.getDealer() != null ? notification.getDealer().getDealerName() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}