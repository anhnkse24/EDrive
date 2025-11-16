package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ManufacturerInventoryRepository manufacturerInventoryRepo;
    private final DealerInventoryRepository dealerInventoryRepo;

    @Override
    @Transactional
    public void confirmDelivery(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Chỉ xử lý orders đang ở trạng thái PROCESSING
        if (order.getStatus() != OrderStatus.ĐÃ_XÁC_NHẬN) {
            throw new IllegalStateException("Only ĐÃ_XÁC_NHẬN orders can be delivered");
        }

        // Cập nhật số lượng kho cho từng item
        for (OrderItem orderItem : order.getOrderItems()) {
            updateInventory(orderItem);
        }

        // Cập nhật trạng thái order
        order.setStatus(OrderStatus.ĐÃ_GIAO);
        order.setActualDeliveryDate(LocalDate.now());
        orderRepo.save(order);
    }

    private void updateInventory(OrderItem orderItem) {
        Vehicle vehicle = orderItem.getVehicle();
        Integer quantity = orderItem.getQuantity();

        // Trừ kho hãng
        ManufacturerInventory manufacturerInventory = manufacturerInventoryRepo
                .findByVehicle_VehicleId(vehicle.getVehicleId())
                .orElseThrow(() -> new IllegalStateException(
                        "Manufacturer inventory not found for vehicle: " + vehicle.getVehicleId()));

        if (manufacturerInventory.getQuantity() < quantity) {
            throw new IllegalStateException(
                    "Insufficient manufacturer inventory for vehicle: " + vehicle.getModelName());
        }

        manufacturerInventory.setQuantity(manufacturerInventory.getQuantity() - quantity);
        manufacturerInventory.setLastUpdated(java.time.LocalDateTime.now());
        manufacturerInventoryRepo.save(manufacturerInventory);

        // Cộng kho đại lý
        Dealer dealer = orderItem.getOrder().getDealer();
        Optional<DealerInventory> dealerInventoryOpt = dealerInventoryRepo
                .findByDealer_DealerIdAndVehicle_VehicleId(dealer.getDealerId(), vehicle.getVehicleId());

        DealerInventory dealerInventory;
        if (dealerInventoryOpt.isPresent()) {
            dealerInventory = dealerInventoryOpt.get();
            dealerInventory.setQuantity(dealerInventory.getQuantity() + quantity);
        } else {
            dealerInventory = DealerInventory.builder()
                    .dealer(dealer)
                    .vehicle(vehicle)
                    .quantity(quantity)
                    .lastUpdated(java.time.LocalDateTime.now())
                    .build();
        }

        dealerInventoryRepo.save(dealerInventory);
    }
}