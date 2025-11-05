package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.request.OrderItemRequest;
import com.swp391.edrive.dto.response.OrderItemResponse;
import com.swp391.edrive.dto.response.OrderResponse;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.NotificationService;
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
    private final NotificationService notificationService;

    @Value("${edrive.vat-rate:0.1}")
    private BigDecimal vatRate;

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepo.findByStatus(status);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered order cannot be cancelled");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Paid order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        orderRepo.save(order);

        // (tuỳ chọn) hoàn kho hãng nếu bạn muốn: duyệt order.getOrderItems() và cộng tồn lại.

        return mapToOrderResponse(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getOrderId());
        res.setDealerId(order.getDealer().getDealerId());
        res.setDealerName(order.getDealer().getDealerName());
        res.setOrderDate(order.getOrderDate());
        res.setDesiredDeliveryDate(order.getDesiredDeliveryDate());
        res.setActualDeliveryDate(order.getActualDeliveryDate());
        res.setSubtotal(order.getSubtotal());
        res.setTotalDiscount(order.getTotalDiscount());
        res.setVatAmount(order.getVatAmount());
        res.setTotalPrice(order.getTotalPrice());
        res.setOrderStatus(order.getStatus());
        res.setDeliveryAddress(order.getDeliveryAddress());
        res.setDeliveryNote(order.getDeliveryNote());

        if (order.getOrderItems() != null) {
            res.setOrderItems(
                    order.getOrderItems().stream().map(item -> {
                        var itemRes = new com.swp391.edrive.dto.response.OrderItemResponse();
                        itemRes.vehicleId = item.getVehicle().getVehicleId();
                        itemRes.vehicleName = item.getVehicle().getModelName();
                        itemRes.quantity = item.getQuantity();
                        itemRes.unitPrice = item.getUnitPrice();
                        itemRes.itemSubtotal = item.getUnitPrice().multiply(
                                java.math.BigDecimal.valueOf(item.getQuantity())
                        );
                        itemRes.itemDiscount = item.getDiscountAmount();
                        itemRes.itemTotal = item.getTotalPrice();
                        return itemRes;
                    }).toList()
            );
        }
        return res;
    }

    @Override
    public List<OrderResponse> getOrdersByDealerId(Long dealerId) {
        // kiểm tra dealer tồn tại
        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found with id: " + dealerId));

        // lấy danh sách order theo dealer
        List<Order> orders = orderRepo.findByDealer_DealerId(dealerId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }
    @Override
    @Transactional
    public OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId) {
        validate(req);  // Kiểm tra tính hợp lệ của dữ liệu đầu vào

        // Lấy thông tin Dealer từ dealerId
        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên
        order.setOrderDate(LocalDate.now());  // Ngày đặt hàng
        order.setDealer(dealer);  // Đặt dealer từ đối tượng đã tìm thấy
        order.setStatus(OrderStatus.PENDING);  // Trạng thái đơn hàng

        order.setDesiredDeliveryDate(req.getDesiredDeliveryDate());  // Ngày giao hàng mong muốn
        order.setDeliveryAddress(req.getDeliveryAddress());  // Địa chỉ giao hàng
        order.setDeliveryNote(req.getDeliveryNote());  // Ghi chú giao hàng

        // Xử lý các item trong đơn hàng
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalSubtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : req.getOrderItems()) {
            Vehicle vehicle = vehicleRepo.findById(itemReq.getVehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + itemReq.getVehicleId()));

            // Kiểm tra tồn kho của nhà sản xuất
            validateManufacturerInventory(vehicle, itemReq.getQuantity());

            BigDecimal unitPrice = vehicle.getPriceRetail();
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());
            BigDecimal itemSubtotal = unitPrice.multiply(quantity);

            // Tính chiết khấu
            BigDecimal discountRate = calculateDiscountRate(itemReq.getQuantity());
            BigDecimal itemDiscount = itemSubtotal.multiply(discountRate);
            BigDecimal itemTotal = itemSubtotal.subtract(itemDiscount);

            totalSubtotal = totalSubtotal.add(itemSubtotal);
            totalDiscount = totalDiscount.add(itemDiscount);

            // Tạo item cho đơn hàng
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

        // Lưu đơn hàng và các item
        Order savedOrder = orderRepo.save(order);
        orderItemRepo.saveAll(orderItems);

        notificationService.createAdminNotificationForDealerOrder(order.getOrderId());

        // Trả về thông tin chi tiết đơn hàng
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