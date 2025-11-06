package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.OrderCustomerRequest;
import com.swp391.edrive.dto.request.StatusOrderCustomerRequest;
import com.swp391.edrive.dto.response.OrderCustomerResponse;
import com.swp391.edrive.entity.Color;
import com.swp391.edrive.entity.OrderCustomer;
import com.swp391.edrive.entity.StatusOrderCustomer;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.OrderCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCustomerServiceImpl implements OrderCustomerService {

    private final OrderCustomerRepository orderCustomerRepository;
    private final StatusOrderCustomerRepository statusOrderCustomerRepository;
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final ColorRepository colorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderCustomerResponse> getAllOrders() {
        return orderCustomerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderCustomerResponse> getOrdersByCustomerId(Long customerId) {
        return orderCustomerRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderCustomerResponse> getOrderById(Long id) {
        return orderCustomerRepository.findById(id)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public OrderCustomerResponse createOrder(OrderCustomerRequest request) {
        var customer = customerRepository.findByFullNameAndPhone(
                        request.getCustomerName(),
                        request.getCustomerPhone())
                .or(() -> customerRepository.findByPhone(request.getCustomerPhone()))
                .or(() -> customerRepository.findByFullName(request.getCustomerName()))
                .orElseThrow(() -> new RuntimeException(
                        "❌ Không tìm thấy khách hàng với tên: " + request.getCustomerName() +
                                " hoặc số điện thoại: " + request.getCustomerPhone()
                ));
        var vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy xe với ID: " + request.getVehicleId()));

        var dealer = dealerRepository.findById(request.getDealerId())
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy đại lý với ID: " + request.getDealerId()));

        customer.setFullName(request.getCustomerName());
        customer.setPhone(request.getCustomerPhone());
        customerRepository.save(customer);

        OrderCustomer order = new OrderCustomer();
        order.setOrderCode("ORD-" + System.currentTimeMillis()); // tạo mã đơn hàng tạm
        order.setCustomer(customer);
        order.setVehicle(vehicle);
        order.setDealer(dealer);

        StatusOrderCustomer statusOrder = StatusOrderCustomer.builder()
                .status(request.getStatus())
                .deliveryDate(request.getDeliveryDate())
                .deliveryLocation(request.getDeliveryLocation())
                .orderCustomer(order)
                .build();

        order.setStatusOrderCustomer(statusOrder);

        OrderCustomer saved = orderCustomerRepository.save(order);

        log.info("✅ Created new order: {} for customer {}", saved.getOrderCode(), customer.getFullName());

        return toResponse(saved);
    }

    @Transactional
    public OrderCustomerResponse updateOrder(Long id, OrderCustomerRequest req) {
        OrderCustomer order = orderCustomerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy đơn hàng với ID: " + id));

        if (order.getCustomer() != null) {
            if (req.getCustomerName() != null)
                order.getCustomer().setFullName(req.getCustomerName());
            if (req.getCustomerPhone() != null)
                order.getCustomer().setPhone(req.getCustomerPhone());
        }

        if (req.getVehicleId() != null) {
            var vehicle = vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy xe với ID: " + req.getVehicleId()));
            order.setVehicle(vehicle);
        }

        if (req.getColor() != null && order.getVehicle() != null) {
            Color color = resolveColor(req.getColor());
            order.getVehicle().setColor(color);
        }

        if (req.getDealerId() != null) {
            var dealer = dealerRepository.findById(req.getDealerId())
                    .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy đại lý với ID: " + req.getDealerId()));
            order.setDealer(dealer);
        }

        StatusOrderCustomer status = order.getStatusOrderCustomer();
        if (status == null) {
            status = new StatusOrderCustomer();
            status.setOrderCustomer(order);
        }

        if (req.getStatus() != null) status.setStatus(req.getStatus());
        if (req.getDeliveryDate() != null) status.setDeliveryDate(req.getDeliveryDate());
        if (req.getDeliveryLocation() != null) status.setDeliveryLocation(req.getDeliveryLocation());

        statusOrderCustomerRepository.save(status);
        order.setStatusOrderCustomer(status);

        orderCustomerRepository.save(order);

        log.info("✅ Updated order {} via OrderCustomerRequest", id);
        return toResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderCustomerRepository.existsById(id)) {
            throw new RuntimeException("❌ Không tìm thấy đơn hàng với ID: " + id);
        }
        orderCustomerRepository.deleteById(id);
        log.warn("🗑 Deleted order with ID = {}", id);
    }

    @Override
    @Transactional
    public StatusOrderCustomer updateOrderStatus(Long orderId, StatusOrderCustomerRequest request) {
        // 🔍 Tìm đơn hàng
        OrderCustomer order = orderCustomerRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy đơn hàng với ID: " + orderId));

        StatusOrderCustomer status = order.getStatusOrderCustomer();
        if (status == null) {
            status = new StatusOrderCustomer();
            status.setOrderCustomer(order);
        }

        status.setStatus(request.getStatus());
        status.setDeliveryDate(request.getDeliveryDate());
        status.setDeliveryLocation(request.getDeliveryLocation());

        StatusOrderCustomer updatedStatus = statusOrderCustomerRepository.save(status);

        order.setStatusOrderCustomer(updatedStatus);
        orderCustomerRepository.save(order);

        log.info("🔄 Cập nhật trạng thái đơn hàng ID = {} -> {}", orderId, request.getStatus());
        return updatedStatus;
    }

    private OrderCustomerResponse toResponse(OrderCustomer order) {
        return OrderCustomerResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())

                .customerName(order.getCustomer() != null ? order.getCustomer().getFullName() : null)
                .customerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : null)

                .vehicleName(order.getVehicle() != null ? order.getVehicle().getModelName() : null)
                .color(
                        order.getVehicle() != null && order.getVehicle().getColor() != null
                                ? order.getVehicle().getColor().getColorName()
                                : null
                )
                .dealerName(order.getDealer() != null ? order.getDealer().getDealerName() : null)
                .dealerPhone(order.getDealer() != null ? order.getDealer().getPhone() : null)

                .deliveryDate(order.getStatusOrderCustomer() != null ? order.getStatusOrderCustomer().getDeliveryDate() : null)
                .status(order.getStatusOrderCustomer() != null ? order.getStatusOrderCustomer().getStatus() : null)
                .deliveryLocation(order.getStatusOrderCustomer() != null ? order.getStatusOrderCustomer().getDeliveryLocation() : null)

                .build();
    }

    private Color resolveColor(String colorNameOrNull) {
        if (colorNameOrNull == null || colorNameOrNull.isBlank()) return null;
        return colorRepository.findByColorNameIgnoreCase(colorNameOrNull.trim())
                .orElseGet(() -> colorRepository.save(
                        Color.builder().colorName(colorNameOrNull.trim()).build()
                ));
    }
}
