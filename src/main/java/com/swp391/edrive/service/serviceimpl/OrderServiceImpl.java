package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.OrderCreateRequest;
import com.swp391.edrive.dto.response.OrderSummaryResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentStatus;
import com.swp391.edrive.enums.PaymentType;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final DealerRepository dealerRepo;
    private final QuotationRepository quotationRepo;
    private final PaymentRepository paymentRepo;

    @Value("${edrive.vat-rate:0.1}") // 10% mặc định
    private BigDecimal vatRate;

    public OrderServiceImpl(OrderRepository orderRepo,
                            DealerRepository dealerRepo,
                            QuotationRepository quotationRepo,
                            PaymentRepository paymentRepo) {
        this.orderRepo = orderRepo;
        this.dealerRepo = dealerRepo;
        this.quotationRepo = quotationRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional
    public OrderSummaryResponse createOrderByDealerCashOnly(OrderCreateRequest req, Long dealerId) {
        validate(req);

        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));

        // 1) Lấy đơn giá
        BigDecimal unitPrice = resolveUnitPrice(req);

        // 2) Tính tiền: Subtotal -> Discount -> VAT -> GrandTotal
        BigDecimal qty = BigDecimal.valueOf(req.quantity);
        BigDecimal subtotal = unitPrice.multiply(qty);

        BigDecimal dealerDiscount = calcDealerDiscount(dealer, req.quantity, subtotal);
        BigDecimal taxable = subtotal.subtract(dealerDiscount);
        if (taxable.signum() < 0) taxable = BigDecimal.ZERO;

        BigDecimal vatAmount = taxable.multiply(vatRate).setScale(0, RoundingMode.HALF_UP);
        BigDecimal grandTotal = taxable.add(vatAmount);

        // 3) Lưu Order
        Order o = new Order();
        o.setOrderDate(LocalDate.now());
        o.setDealer(dealer);
        o.setStatus(OrderStatus.PENDING);                    // tạo đơn
        o.setPaymentMethod(PaymentType.FULL);               // field của bạn là PaymentType (không phải PaymentMethod)
        o.setPaymentStatus(PaymentStatus.PAID);             // thu tiền mặt ngay
        o.setTotalPrice(grandTotal.doubleValue());          // entity đang dùng Double

        if (req.quotationId != null) {
            Quotation q = quotationRepo.findById(req.quotationId)
                    .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
            o.setQuotation(q);
        }
        // Nếu Order có các field giao hàng, thêm vào entity và mở set ở đây:
        // o.setDesiredDeliveryDate(req.desiredDeliveryDate);
        // o.setDeliveryAddress(req.deliveryAddress);
        // o.setDeliveryNote(req.deliveryNote);

        orderRepo.save(o);

        // 4) Ghi nhận Payment (CASH, FULL, PAID)
        Payment p = new Payment();
        p.setOrder(o);
        p.setAmount(grandTotal.doubleValue());
        p.setPaymentDate(LocalDate.now());
        p.setPaymentType(PaymentType.FULL);
        p.setMethod(PaymentMethod.CASH);
        p.setStatus(PaymentStatus.PAID);
        paymentRepo.save(p);

        // 5) Cập nhật Order status sang PROCESSING (đã thanh toán, chờ giao)
        o.setStatus(OrderStatus.PROCESSING);
        orderRepo.save(o);

        // 6) Response
        OrderSummaryResponse res = new OrderSummaryResponse();
        res.orderId = o.getOrderId();
        res.unitPrice = unitPrice;
        res.quantity = req.quantity;
        res.subtotal = subtotal;
        res.dealerDiscount = dealerDiscount;
        res.vatAmount = vatAmount;
        res.grandTotal = grandTotal;
        res.desiredDeliveryDate = req.desiredDeliveryDate;
        res.deliveryAddress = req.deliveryAddress;
        res.deliveryNote = req.deliveryNote;
        res.orderStatus = o.getStatus().name();
        res.paymentMethod = PaymentMethod.CASH.name();
        res.paymentStatus = o.getPaymentStatus().name();
        return res;
    }

    private void validate(OrderCreateRequest req) {
        if (req.quantity == null || req.quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        if (req.desiredDeliveryDate != null && req.desiredDeliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("desiredDeliveryDate cannot be in the past");
        }
        if (req.quotationId == null && req.vehicleId == null) {
            throw new IllegalArgumentException("Either quotationId or vehicleId is required");
        }
    }

    private BigDecimal resolveUnitPrice(OrderCreateRequest req) {
        if (req.quotationId != null) {
            Quotation q = quotationRepo.findById(req.quotationId)
                    .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
            // dùng quotedPrice từ Quotation (Double -> BigDecimal)
            if (q.getQuotedPrice() == null) {
                throw new IllegalStateException("Quotation.quotedPrice is null");
            }
            return BigDecimal.valueOf(q.getQuotedPrice());
        }
        // Nếu không có quotationId mà có vehicleId:
        // Chưa có thông tin getter giá ở Vehicle -> cần bạn bổ sung Vehicle entity hoặc field giá.
        throw new IllegalStateException("Vehicle pricing not implemented yet. Please provide Vehicle entity with a price field (e.g., getListPrice() or getPrice()).");
    }

    private BigDecimal calcDealerDiscount(Dealer dealer, int quantity, BigDecimal subtotal) {
        // Ví dụ bậc thang số lượng; thay bằng chính sách thật của bạn nếu có
        BigDecimal rate = BigDecimal.ZERO;
        if (quantity >= 10) rate = new BigDecimal("0.05");
        else if (quantity >= 5) rate = new BigDecimal("0.03");
        else if (quantity >= 2) rate = new BigDecimal("0.01");
        return subtotal.multiply(rate).setScale(0, RoundingMode.HALF_UP);
    }

}
