package com.swp391.edrive.service.serviceimpl;


import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.Payment;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentStatus;
import com.swp391.edrive.exception.exceptions.ResourceNotFoundException;
import com.swp391.edrive.repository.OrderRepository;
import com.swp391.edrive.repository.PaymentRepository;
import com.swp391.edrive.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // ====== Properties theo dự án mẫu ======
    @Value("${payment.vnpay.tmn-code}")   private String vnpTmnCode;
    @Value("${payment.vnpay.secret-key}") private String vnpSecretKey;
    @Value("${payment.vnpay.url}")        private String vnpUrl;
    @Value("${payment.vnpay.ip-address}") private String vnpIpAddress;
    @Value("${frontend.url.payment.return}") private String vnpReturnUrl;

    private static final String VNPAY_SUCCESS_CODE = "00";
    private static final String PAYMENT_CANCELED_CODE = "24";
    private static final String TRANSACTION_FAILED_CODE = "02";
    private static final String PAYMENT_PENDING_CODE = "91";

    // ====== CASH (đã gửi bạn trước đó, giữ nguyên changeAmount, v.v.) ======

    // ====== VNPay: TẠO LINK (style dự án mẫu) ======
    @Override
    @Transactional
    public ResponseObject createVnPayUrl(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Validate booking status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be paid");
        }

        // Check if payment is expired
        if (order.isPaymentExpired()) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(PaymentStatus.CANCELLED); // NEW
            orderRepository.save(order);
            throw new IllegalStateException("Payment time has expired");
        }

        try {
            String vnpUrl = createVNPayPaymentUrl(order);
            return new ResponseObject(
                    HttpStatus.OK.value(),
                    "VNPay URL created successfully",
                    Map.of(
                            "vnpayUrl", vnpUrl,
                            "expiryTime", order.getPaymentExpiryTime(),
                            "remainingMinutes",
                            Duration.between(LocalDateTime.now(), order.getPaymentExpiryTime())
                                    .toMinutes()));
        } catch (Exception e) {
            throw new RuntimeException("Error creating VNPay URL", e);
        }
    }

    private String createVNPayPaymentUrl(Order order) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedExpireDate = order.getPaymentExpiryTime().format(formatter);

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
        vnpParams.put("vnp_OrderInfo", "Payment for booking: " + String.valueOf(order.getOrderId()));
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", String.valueOf(order.getTotalPrice().intValue() * 100));
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_CreateDate", LocalDateTime.now().format(formatter));
        vnpParams.put("vnp_IpAddr", vnpIpAddress);
        vnpParams.put("vnp_ExpireDate", formattedExpireDate);

        String signData = buildSignData(vnpParams);
        vnpParams.put("vnp_SecureHash", generateHMAC(vnpSecretKey, signData));

        return buildPaymentUrl(vnpUrl, vnpParams);
    }

    private String buildSignData(Map<String, String> params) throws Exception {
        StringBuilder signData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            signData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .append('&');
        }
        return signData.deleteCharAt(signData.length() - 1).toString();
    }

    private String generateHMAC(String secretKey, String signData) throws Exception {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        hmacSha512.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8))) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String buildPaymentUrl(String baseUrl, Map<String, String> params) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(baseUrl).append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .append('&');
        }
        return urlBuilder.deleteCharAt(urlBuilder.length() - 1).toString();
    }


    // ====== VNPay: RETURN ======
    @Override
    @Transactional
    public Map<String, String> handleVnPayReturn(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();

        try {
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");

            Order order = orderRepository.findById(vnp_TxnRef)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

            // Check payment expiry
            if (LocalDateTime.now().isAfter(order.getPaymentExpiryTime())) {
                handleExpiredBooking(order);
                return createErrorResponse("98", "Payment time expired");
            }

            // Process payment result
            if (VNPAY_SUCCESS_CODE.equals(vnp_ResponseCode)) {
                processSuccessfulPayment(order, vnp_TxnRef);
                response.put("RspCode", VNPAY_SUCCESS_CODE);
                response.put("Message", "Payment successful");
            } else {
                processFailedPayment(order.getOrderId());
                response.put("RspCode", "99");
                response.put("Message", getVnPayErrorMessage(vnp_ResponseCode));
            }

        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Error processing payment: " + e.getMessage());
        }

        return response;
    }

    private void handleExpiredBooking(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED); // NEW
        orderRepository.save(order);
    }

    private Map<String, String> createErrorResponse(String code, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("RspCode", code);
        response.put("Message", message);
        return response;
    }

    @Transactional
    public void processSuccessfulPayment(Order order, String transactionId) {
        // Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .vnpTxnRef(transactionId)
                .paymentDate(LocalDate.now())
                .status(PaymentStatus.PAID) // Cập nhật status
                .build();

        paymentRepository.save(payment);

        // Update order status to PROCESSING (chờ giao hàng)
        order.setStatus(OrderStatus.PROCESSING);
        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);

        // NOTE: KHÔNG cập nhật kho ở đây, chỉ cập nhật khi giao hàng thành công
    }

    @Transactional
    public void processFailedPayment(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));


        // Cancel booking if payment failed
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }

    private String getVnPayErrorMessage(String responseCode) {
        switch (responseCode) {
            case PAYMENT_CANCELED_CODE:
                return "Payment canceled by user";
            case TRANSACTION_FAILED_CODE:
                return "Transaction failed";
            case PAYMENT_PENDING_CODE:
                return "Payment pending";
            default:
                return "Payment failed with error code: " + responseCode;
        }
    }
}
