package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CreateOrderFromContractRequest;
import com.swp391.edrive.dto.request.UpdateOrderStatusRequest;
import com.swp391.edrive.dto.response.OrderResponse;
import com.swp391.edrive.entity.Contract;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.Payment;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentType;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;
    private final ContractRepository contractRepo;
    private final PaymentRepository paymentRepo;

    @Override
    public OrderResponse createFromContract(CreateOrderFromContractRequest req) {
        Contract c = contractRepo.findById(req.contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        if (c.getStatus() == null || !c.getStatus().name().equals("ACTIVE")) {
            throw new IllegalStateException("Only ACTIVE contract can create order");
        }

        var q = c.getQuotation();
        if (orderRepo.existsByQuotation_Id(q.getId())) {
            throw new IllegalStateException("Order already exists for this quotation");
        }

        Order o = new Order();
        o.setQuotation(q);
        o.setDealer(q.getDealer());
        o.setCustomer(q.getCustomer());
        o.setTotalPrice(c.getContractValue());

        // Order hiện đang lưu PaymentType trong field 'paymentMethod' (naming từ entity của bạn)
        PaymentType type = (req.paymentType != null) ? req.paymentType : PaymentType.FULL;
        o.setPaymentMethod(type);

        o.setStatus(OrderStatus.PENDING);

        // Nếu Contract có field setOrder thì gắn 1-1 (không bắt buộc, bọc try để an toàn)
        try { c.setOrder(o); } catch (Exception ignored) {}

        var saved = orderRepo.save(o);
        // Vừa tạo nên chưa có payment -> truyền list rỗng
        return buildResponse(saved, java.util.Collections.emptyList());
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse get(Long id) {
        var o = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        var payments = paymentRepo.findByOrder_OrderId(o.getOrderId());
        return buildResponse(o, payments);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> list() {
        return orderRepo.findAll().stream().map(o -> {
            var payments = paymentRepo.findByOrder_OrderId(o.getOrderId());
            return buildResponse(o, payments);
        }).toList();
    }

    @Override
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest req) {
        var o = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        var next = OrderStatus.valueOf(req.status.trim().toUpperCase());
        o.setStatus(next);
        var payments = paymentRepo.findByOrder_OrderId(o.getOrderId());
        return buildResponse(o, payments);
    }

    // --------- Helpers ---------

    private OrderResponse buildResponse(Order o, List<Payment> payments) {
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy phương thức thanh toán (PaymentMethod) của lần payment gần nhất (nếu có)
        PaymentMethod lastMethod = payments.stream()
                .max(Comparator
                        .comparing(Payment::getPaymentDate,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Payment::getPaymentId))
                .map(Payment::getMethod)
                .orElse(null);

        var r = new OrderResponse();
        r.orderId = o.getOrderId();

        Long quotationId = (o.getQuotation() != null) ? o.getQuotation().getId() : null;
        r.contractId = null;
        if (quotationId != null) {
            contractRepo.findByQuotation_Id(quotationId).ifPresent(ct -> r.contractId = ct.getContractId());
        }

        r.quotationId = quotationId;
        r.dealerId    = (o.getDealer() != null)   ? o.getDealer().getDealerId()     : null;
        r.customerId  = (o.getCustomer() != null) ? o.getCustomer().getCustomerId() : null;
        r.orderDate   = o.getOrderDate();
        r.totalPrice  = o.getTotalPrice();

        // Trả về đúng ENUM
        r.paymentType   = o.getPaymentMethod(); // đây là PaymentType do entity Order định nghĩa
        r.paymentMethod = lastMethod;           // từ lần Payment gần nhất (vd: CASH), nếu chưa có payment → null

        r.status = o.getStatus();
        r.totalPaid = totalPaid;
        r.remaining = o.getTotalPrice().subtract(totalPaid);
        return r;
    }
}
