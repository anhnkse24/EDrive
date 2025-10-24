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
import lombok.RequiredArgsConstructor;
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
    private final VehicleRepository vehicleRepo; // NEW

    @Value("${edrive.vat-rate:0.1}")
    private BigDecimal vatRate;

    public OrderServiceImpl(OrderRepository orderRepo,
                            DealerRepository dealerRepo,
                            VehicleRepository vehicleRepo) { // CHANGED
        this.orderRepo = orderRepo;
        this.dealerRepo = dealerRepo;
        this.vehicleRepo = vehicleRepo; // NEW
    }

    @Override
    @Transactional
    public OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId) {
        validate(req);

        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));

        // 1) LẤY GIÁ TỪ VEHICLE.priceRetail (BigDecimal)
        var vehicle = vehicleRepo.findById(req.vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: id=" + req.vehicleId));
        BigDecimal unitPrice = vehicle.getPriceRetail();          // <— dùng đúng field của bạn
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalStateException("Vehicle.priceRetail is null or <= 0");
        }

        // 2) TÍNH TIỀN
        BigDecimal qty = BigDecimal.valueOf(req.quantity);
        BigDecimal subtotal = unitPrice.multiply(qty);

        BigDecimal dealerDiscount = calcDealerDiscount(dealer, req.quantity, subtotal);
        BigDecimal priceAfterDiscountBeforeVat = subtotal.subtract(dealerDiscount).max(BigDecimal.ZERO);

        BigDecimal vatAmount = priceAfterDiscountBeforeVat
                .multiply(vatRate)
                .setScale(0, java.math.RoundingMode.HALF_UP);

        BigDecimal priceWithVatBeforeDiscount = subtotal.add(subtotal.multiply(vatRate));
        BigDecimal grandTotal = priceAfterDiscountBeforeVat.add(vatAmount);

        // 3) LƯU ORDER (chưa thanh toán)
        Order o = new Order();
        o.setOrderDate(java.time.LocalDate.now());
        o.setDealer(dealer);
        o.setStatus(com.swp391.edrive.enums.OrderStatus.PENDING);
        o.setPaymentMethod(com.swp391.edrive.enums.PaymentType.FULL);      // field của Order là PaymentType
        o.setPaymentStatus(com.swp391.edrive.enums.PaymentStatus.PENDING); // chưa thanh toán
        o.setTotalPrice(grandTotal.doubleValue());
        orderRepo.save(o);

        // 4) TRẢ RESPONSE HIỂN THỊ CHI TIẾT GIÁ
        OrderSummaryResponse res = new OrderSummaryResponse();
        res.orderId = o.getOrderId();
        res.subtotal = subtotal;
        res.dealerDiscount = dealerDiscount;
        res.vatAmount = vatAmount;
        res.grandTotal = grandTotal;

        res.desiredDeliveryDate = req.desiredDeliveryDate;
        res.deliveryAddress = req.deliveryAddress;
        res.deliveryNote = req.deliveryNote;

        res.orderStatus = o.getStatus().name();
        res.paymentMethod = o.getPaymentMethod().name();   // FULL (kiểu thanh toán)
        res.paymentStatus = o.getPaymentStatus().name();   // PENDING
        return res;
    }

    private void validate(OrderCreateRequest req) {
        if (req.vehicleId == null) throw new IllegalArgumentException("vehicleId is required");
        if (req.quantity == null || req.quantity <= 0)
            throw new IllegalArgumentException("quantity must be > 0");
        if (req.desiredDeliveryDate != null &&
                req.desiredDeliveryDate.isBefore(java.time.LocalDate.now()))
            throw new IllegalArgumentException("desiredDeliveryDate cannot be in the past");
    }

    private BigDecimal calcDealerDiscount(Dealer dealer, int quantity, BigDecimal subtotal) {
        BigDecimal rate = BigDecimal.ZERO;
        if (quantity >= 10) rate = new BigDecimal("0.05");
        else if (quantity >= 5) rate = new BigDecimal("0.03");
        else if (quantity >= 2) rate = new BigDecimal("0.01");
        return subtotal.multiply(rate).setScale(0, java.math.RoundingMode.HALF_UP);
    }
}
