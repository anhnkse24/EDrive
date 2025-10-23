package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CreateCashPaymentRequest;
import com.swp391.edrive.dto.response.PaymentResponse;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.Payment;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentType;
import com.swp391.edrive.repository.OrderRepository;
import com.swp391.edrive.repository.PaymentRepository;
import com.swp391.edrive.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    @Override
    public PaymentResponse createCashPayment(CreateCashPaymentRequest req) {
        if (req.amount == null || req.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        PaymentType type = req.getPaymentType(); // enum trực tiếp
        if (type == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }

        Order o = orderRepo.findById(req.orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Tính tổng đã thu
        BigDecimal collected = paymentRepo.findByOrder_OrderId(o.getOrderId())
                .stream().map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Chặn vượt tổng
        if (collected.add(req.amount).compareTo(o.getTotalPrice()) > 0) {
            throw new IllegalStateException("Total collected exceeds order total");
        }

        Payment p = new Payment();
        p.setOrder(o);
        p.setAmount(req.amount);
        p.setPaymentType(type);
        p.setMethod(PaymentMethod.CASH);
        p.setNote(req.note);
        paymentRepo.save(p);

        // Nếu FULL hoặc đã đủ tiền → chuyển trạng thái
        BigDecimal after = collected.add(req.amount);
        if (after.compareTo(o.getTotalPrice()) == 0) {
            if (o.getStatus() == OrderStatus.PENDING) {
                o.setStatus(OrderStatus.PROCESSING); // hoặc DELIVERED
            }
        }

        return toResponse(p);
    }

    @Transactional(readOnly = true)
    @Override
    public java.util.List<PaymentResponse> listByOrder(Long orderId) {
        return paymentRepo.findByOrder_OrderId(orderId).stream().map(this::toResponse).toList();
    }

    private PaymentResponse toResponse(Payment p) {
        var r = new PaymentResponse();
        r.paymentId = p.getPaymentId();
        r.orderId = p.getOrder().getOrderId();
        r.amount = p.getAmount();
        r.paymentDate = p.getPaymentDate();
        r.paymentType = p.getPaymentType();
        r.method = p.getMethod();
        r.note = p.getNote();
        return r;
    }
}
