package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.request.OrderItemRequest;
import com.swp391.edrive.dto.response.OrderItemResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final DealerRepository dealerRepo;
    private final VehicleRepository vehicleRepo;
    private final OrderItemRepository orderItemRepo;
    private final ManufacturerInventoryRepository manufacturerInventoryRepo;

    @Value("${edrive.vat-rate:0.1}")
    private BigDecimal vatRate;

    @Override
    @Transactional
    public OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId) {
        validate(req);

        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));

        // Tạo order mới
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        order.setDealer(dealer);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setDesiredDeliveryDate(req.getDesiredDeliveryDate());
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setDeliveryNote(req.getDeliveryNote());

        // Xử lý từng item trong order
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalSubtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : req.getOrderItems()) {
            Vehicle vehicle = vehicleRepo.findById(itemReq.getVehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + itemReq.getVehicleId()));

            // Validate số lượng trong kho hãng
            validateManufacturerInventory(vehicle, itemReq.getQuantity());

            BigDecimal unitPrice = vehicle.getPriceRetail();
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());
            BigDecimal itemSubtotal = unitPrice.multiply(quantity);

            // Tính chiết khấu theo số lượng
            BigDecimal discountRate = calculateDiscountRate(itemReq.getQuantity());
            BigDecimal itemDiscount = itemSubtotal.multiply(discountRate);

            BigDecimal itemTotal = itemSubtotal.subtract(itemDiscount);

            totalSubtotal = totalSubtotal.add(itemSubtotal);
            totalDiscount = totalDiscount.add(itemDiscount);

            // Tạo order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVehicle(vehicle);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setDiscountRate(discountRate);
            orderItem.setDiscountAmount(itemDiscount);
            orderItem.setTotalPrice(itemTotal);

            orderItems.add(orderItem);
        }

        // Tính VAT và tổng tiền
        BigDecimal amountAfterDiscount = totalSubtotal.subtract(totalDiscount);
        BigDecimal vatAmount = amountAfterDiscount.multiply(vatRate)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal grandTotal = amountAfterDiscount.add(vatAmount);

        order.setSubtotal(totalSubtotal);
        order.setTotalDiscount(totalDiscount);
        order.setVatAmount(vatAmount);
        order.setTotalPrice(grandTotal);

        // Lưu order và order items
        Order savedOrder = orderRepo.save(order);
        orderItemRepo.saveAll(orderItems);

        // Tạo response
        return buildOrderSummaryResponse(savedOrder, orderItems);
    }

    private void validate(OrderCreateRequest req) {
        if (req.getOrderItems() == null || req.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        for (OrderItemRequest item : req.getOrderItems()) {
            if (item.getVehicleId() == null) {
                throw new IllegalArgumentException("vehicleId is required for all items");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("quantity must be > 0 for all items");
            }
        }

        if (req.getDesiredDeliveryDate() != null &&
                req.getDesiredDeliveryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("desiredDeliveryDate cannot be in the past");
        }
    }

    private void validateManufacturerInventory(Vehicle vehicle, Integer quantity) {
        ManufacturerInventory inventory = manufacturerInventoryRepo
                .findByVehicle_VehicleId(vehicle.getVehicleId())
                .orElseThrow(() -> new IllegalStateException(
                        "Vehicle not available in manufacturer inventory: " + vehicle.getVehicleId()));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalStateException(
                    "Insufficient inventory for vehicle " + vehicle.getModelName() +
                            ". Available: " + inventory.getQuantity() + ", Requested: " + quantity);
        }
    }

    private BigDecimal calculateDiscountRate(Integer quantity) {
        if (quantity > 10) {
            return new BigDecimal("0.15"); // 15%
        } else if (quantity >= 6) {
            return new BigDecimal("0.10"); // 10%
        } else if (quantity >= 1) {
            return new BigDecimal("0.05"); // 5%
        }
        return BigDecimal.ZERO;
    }

    private OrderSummaryResponse buildOrderSummaryResponse(Order order, List<OrderItem> orderItems) {
        OrderSummaryResponse response = new OrderSummaryResponse();
        response.orderId = order.getOrderId();
        response.subtotal = order.getSubtotal();
        response.dealerDiscount = order.getTotalDiscount();
        response.vatAmount = order.getVatAmount();
        response.grandTotal = order.getTotalPrice();
        response.desiredDeliveryDate = order.getDesiredDeliveryDate();
        response.deliveryAddress = order.getDeliveryAddress();
        response.deliveryNote = order.getDeliveryNote();
        response.orderStatus = order.getStatus().name();
        response.paymentStatus = order.getPaymentStatus().name();

        // Build item details
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : orderItems) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.vehicleId = item.getVehicle().getVehicleId();
            itemResponse.vehicleName = item.getVehicle().getModelName();
            itemResponse.quantity = item.getQuantity();
            itemResponse.unitPrice = item.getUnitPrice();
            itemResponse.itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemResponse.itemDiscount = item.getDiscountAmount();
            itemResponse.itemTotal = item.getTotalPrice();
            itemResponses.add(itemResponse);
        }

        response.orderItems = itemResponses;
        return response;
    }
}