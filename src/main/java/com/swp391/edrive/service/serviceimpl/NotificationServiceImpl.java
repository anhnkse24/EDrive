package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.TestDriveStatusManagerRequest;
import com.swp391.edrive.dto.request.TestDriveStatusStaffRequest;
import com.swp391.edrive.dto.response.NotificationResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.TestDriveStatusManager;
import com.swp391.edrive.enums.TestDriveStatusStaff;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.NotificationRepository;
import com.swp391.edrive.repository.OrderRepository;
import com.swp391.edrive.repository.UserRepository;
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
    private final UserRepository userRepository;


    @Override
    public void createNotificationForTestDrive(Long dealerId, Long testDriveId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new EntityNotFoundException("Dealer not found"));

        Notification notification = Notification.builder()
                .dealer(dealer)
                .title("Lịch lái thử mới")
                .receiverType("DEALER_MANAGER")
                .message("Bạn có một lịch lái thử mới với mã #" + testDriveId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public void createNotificationForTestDriveStatusForStaff(
            TestDrive testDrive,
            TestDriveStatusStaffRequest request,
            Long staffUserId
    ) {
        String statusMessage = getStatusMessageStaff(request.getStatusOfStaff());

        String message = String.format(
                "Nhân viên đã cập nhật lịch lái thử xe %s của khách hàng %s sang trạng thái: %s",
                testDrive.getVehicle().getModelName(),
                testDrive.getCustomer().getFullName(),
                statusMessage
        );

        if (request.getStatusOfStaff() == TestDriveStatusStaff.CANCELLED
                && request.getCancelReason() != null) {
            message += ". Lý do: " + request.getCancelReason();
        }

        Notification notification = Notification.builder()
                .dealer(testDrive.getDealer())
                .user(null) // Manager nhận → không gán user cố định
                .title("Nhân viên cập nhật lịch lái thử")
                .receiverType("DEALER_MANAGER")
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public void createNotificationForTestDriveStatusForManager(
            TestDrive testDrive,
            TestDriveStatusManagerRequest request,
            Long staffUserId
    ) {

        String statusMessage = getStatusMessageManager(request.getStatusOfManager());

        String message = String.format(
                "Quản lý đã cập nhật lịch lái thử xe %s của khách hàng %s sang trạng thái: %s",
                testDrive.getVehicle().getModelName(),
                testDrive.getCustomer().getFullName(),
                statusMessage
        );

        if (request.getStatusOfManager() == TestDriveStatusManager.CANCELLED
                && request.getCancelReason() != null) {
            message += ". Lý do: " + request.getCancelReason();
        }

        User staff = userRepository.findById(staffUserId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        Notification notification = Notification.builder()
                .dealer(testDrive.getDealer())
                .user(staff)
                .title("Quản lý cập nhật lịch lái thử")
                .receiverType("DEALER_STAFF")
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    private String getStatusMessageManager(TestDriveStatusManager status) {
        switch (status) {
            case PENDING: return "Đang chờ xử lý";
            case APPROVED: return "Đã phê duyệt";
            case COMPLETED: return "Đã hoàn thành";
            case CANCELLED: return "Đã hủy";
            default: return status.toString();
        }
    }
    private String getStatusMessageStaff(TestDriveStatusStaff status) {
        switch (status) {
            case PENDING: return "Đang chờ";
            case COMPLETED: return "Đã hoàn thành";
            case CANCELLED: return "Đã hủy";
            default: return status.toString();
        }
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
    public void createNotificationForDeliveryConfirmed(Order order) {
        Notification notification = Notification.builder()
                .dealer(order.getDealer())
                .receiverType("DEALER_MANAGER")
                .title("Giao hàng thành công")
                .message("Đơn hàng #" + order.getOrderId() + " đã được giao thành công.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
    @Override
    public void createNotificationForUploadedContract(Contract contract) {
        Dealer dealer = contract.getDealer();
        if (dealer == null) return;

        Notification notification = Notification.builder()
                .dealer(dealer)
                .title("Hợp đồng mới từ nhà sản xuất")
                .receiverType("DEALER_MANAGER")
                .message("Hợp đồng cho đơn hàng #" + contract.getOrder().getOrderId() + " đã được upload.")
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
    public List<NotificationResponse> getNotificationsForDealerManager(Long dealerId) {
        List<Notification> notifications = notificationRepository
                .findByDealerDealerIdAndReceiverTypeOrderByCreatedAtDesc(dealerId, "DEALER_MANAGER");

        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<NotificationResponse> getNotificationsForDealerStaff(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    //----------------------------------------------
    private String getStatusMessage(TestDriveStatusManager status) {
        switch (status) {
            case PENDING: return "Đang chờ xử lý";
            case APPROVED: return "Đã phê duyệt";
            case COMPLETED: return "Đã hoàn thành";
            case CANCELLED: return "Đã hủy";
            default: return status.toString();
        }
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