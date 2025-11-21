package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.DeliveryService;
import com.swp391.edrive.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ManufacturerInventoryRepository manufacturerInventoryRepo;
    private final DealerInventoryRepository dealerInventoryRepo;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void confirmDelivery(String orderId) {
        log.info("=== BẮT ĐẦU confirmDelivery cho orderId: {} ===", orderId);

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        log.info("Order status: {}, Payment status: {}", order.getStatus(), order.getPaymentStatus());
        log.info("Số lượng items trong order: {}", order.getOrderItems().size());

        // Validation 0: NGĂN CHẶN GỌI LẠI - Nếu đã ĐÃ_GIAO rồi thì không cho gọi lại
        if (order.getStatus() == OrderStatus.ĐÃ_GIAO) {
            log.warn("CẢNH BÁO: Đơn hàng {} đã được giao rồi! Ngăn chặn gọi lại.", orderId);
            throw new IllegalStateException("Đơn hàng này đã được giao rồi. Không thể giao lại!");
        }

        // Validation 1: Chỉ xử lý orders đang ở trạng thái ĐÃ_XÁC_NHẬN
        if (order.getStatus() != OrderStatus.ĐÃ_XÁC_NHẬN) {
            throw new IllegalStateException("Chỉ có thể giao hàng cho đơn hàng đã được xác nhận. Trạng thái hiện tại: " + order.getStatus());
        }

        // Validation 2: Kiểm tra order đã được thanh toán chưa (đã qua markOrderAsPaid)
        if (order.getPaymentStatus() != PaymentStatus.ĐÃ_THANH_TOÁN) {
            throw new IllegalStateException("Không thể giao hàng cho đơn hàng chưa được thanh toán. Vui lòng xác nhận thanh toán trước.");
        }

        // Validation 3: Kiểm tra tồn kho hãng CÓ ĐỦ HÀNG trước khi bắt đầu xử lý
        log.info("Kiểm tra tồn kho hãng...");
        for (OrderItem orderItem : order.getOrderItems()) {
            Vehicle vehicle = orderItem.getVehicle();
            Integer quantity = orderItem.getQuantity();

            ManufacturerInventory manufacturerInventory = manufacturerInventoryRepo
                    .findByVehicle_VehicleId(vehicle.getVehicleId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Không tìm thấy tồn kho hãng cho xe: " + vehicle.getModelName()));

            log.info("Xe: {} - Yêu cầu: {}, Có sẵn trong kho hãng: {}",
                    vehicle.getModelName(), quantity, manufacturerInventory.getQuantity());

            if (manufacturerInventory.getQuantity() < quantity) {
                throw new IllegalStateException(
                        "Không đủ tồn kho hãng cho xe " + vehicle.getModelName() +
                                ". Có sẵn: " + manufacturerInventory.getQuantity() +
                                ", Yêu cầu: " + quantity);
            }
        }

        // Cập nhật số lượng kho cho từng item (trừ kho hãng, cộng kho đại lý)
        log.info("Bắt đầu cập nhật kho...");
        for (OrderItem orderItem : order.getOrderItems()) {
            updateInventory(orderItem);
        }

        // Cập nhật trạng thái order
        log.info("Cập nhật trạng thái order thành ĐÃ_GIAO");
        order.setStatus(OrderStatus.ĐÃ_GIAO);
        order.setActualDeliveryDate(LocalDate.now());
        orderRepo.save(order);
        notificationService.createNotificationForDeliveryConfirmed(order);

        log.info("=== KẾT THÚC confirmDelivery cho orderId: {} ===", orderId);
    }

    private void updateInventory(OrderItem orderItem) {
        Vehicle vehicle = orderItem.getVehicle();
        Integer quantity = orderItem.getQuantity();

        log.info("--- Cập nhật kho cho xe: {} (vehicleId: {}) ---", vehicle.getModelName(), vehicle.getVehicleId());

        // Trừ kho hãng
        ManufacturerInventory manufacturerInventory = manufacturerInventoryRepo
                .findByVehicle_VehicleId(vehicle.getVehicleId())
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy tồn kho hãng cho xe: " + vehicle.getModelName()));

        int oldManufacturerQty = manufacturerInventory.getQuantity();
        log.info("Kho hãng TRƯỚC KHI TRỪ: {}", oldManufacturerQty);

        // Double check (đã check ở trên nhưng check thêm để chắc chắn)
        if (manufacturerInventory.getQuantity() < quantity) {
            throw new IllegalStateException(
                    "Không đủ tồn kho hãng cho xe: " + vehicle.getModelName());
        }

        // Trừ số lượng từ kho hãng
        int newManufacturerQty = manufacturerInventory.getQuantity() - quantity;
        manufacturerInventory.setQuantity(newManufacturerQty);
        manufacturerInventory.setLastUpdated(java.time.LocalDateTime.now());
        manufacturerInventoryRepo.save(manufacturerInventory);
        log.info("Kho hãng SAU KHI TRỪ: {} (trừ {})", newManufacturerQty, quantity);

        // Cộng vào kho đại lý
        Dealer dealer = orderItem.getOrder().getDealer();
        log.info("Đại lý: {} (dealerId: {})", dealer.getDealerName(), dealer.getDealerId());

        Optional<DealerInventory> dealerInventoryOpt = dealerInventoryRepo
                .findByDealer_DealerIdAndVehicle_VehicleId(dealer.getDealerId(), vehicle.getVehicleId());

        DealerInventory dealerInventory;
        if (dealerInventoryOpt.isPresent()) {
            // Đã có trong kho đại lý - cộng thêm số lượng
            dealerInventory = dealerInventoryOpt.get();
            int oldDealerQty = dealerInventory.getQuantity();
            log.info("Kho đại lý TRƯỚC KHI CỘNG: {}", oldDealerQty);

            int newDealerQty = dealerInventory.getQuantity() + quantity;
            dealerInventory.setQuantity(newDealerQty);
            dealerInventory.setLastUpdated(java.time.LocalDateTime.now());
            log.info("Kho đại lý SAU KHI CỘNG: {} (cộng {})", newDealerQty, quantity);
        } else {
            // Chưa có trong kho đại lý - tạo mới
            log.info("Kho đại lý CHƯA CÓ xe này - tạo mới với số lượng: {}", quantity);
            dealerInventory = DealerInventory.builder()
                    .dealer(dealer)
                    .vehicle(vehicle)
                    .quantity(quantity)
                    .lastUpdated(java.time.LocalDateTime.now())
                    .build();
        }

        dealerInventoryRepo.save(dealerInventory);
        log.info("--- Hoàn thành cập nhật kho cho xe: {} ---", vehicle.getModelName());
    }
}