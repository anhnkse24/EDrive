package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CashPaymentRequest;
import com.swp391.edrive.dto.request.VnPayLinkRequest;
import com.swp391.edrive.dto.response.CashPaymentResponse;
import com.swp391.edrive.dto.response.VnPayLinkResponse;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.Payment;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentStatus;
import com.swp391.edrive.enums.PaymentType;
import com.swp391.edrive.repository.OrderRepository;
import com.swp391.edrive.repository.PaymentRepository;
import com.swp391.edrive.service.PaymentService;
import com.swp391.edrive.util.VnPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    // ====== Properties theo dự án mẫu ======
    @Value("${payment.vnpay.tmn-code}")   private String vnpTmnCode;
    @Value("${payment.vnpay.secret-key}") private String vnpSecretKey;
    @Value("${payment.vnpay.url}")        private String vnpUrl;
    @Value("${payment.vnpay.ip-address}") private String vnpIp;
    @Value("${frontend.url.payment.return}") private String vnpReturnUrl;

    private static final String VNP_VERSION  = "2.1.0";
    private static final String VNP_COMMAND  = "pay";
    private static final String VNP_CURR     = "VND";
    private static final String VNP_LOCALE   = "vn";
    private static final String VNP_ORDTYPE  = "other";
    private static final String SUCCESS_CODE = "00";

    // ====== CASH (đã gửi bạn trước đó, giữ nguyên changeAmount, v.v.) ======
    @Override
    @Transactional
    public CashPaymentResponse payCash(CashPaymentRequest req) {
        if (req.orderId == null) throw new IllegalArgumentException("orderId is required");

        Order o = orderRepo.findById(req.orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        BigDecimal grandTotal = BigDecimal.valueOf(o.getTotalPrice());
        BigDecimal collected = paymentRepo.findByOrder_OrderId(o.getOrderId())
                .stream().map(p -> BigDecimal.valueOf(p.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = grandTotal.subtract(collected);
        BigDecimal payNow = (req.amount == null) ? remaining : req.amount;
        if (payNow.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");

        BigDecimal changeAmount = BigDecimal.ZERO;
        if (payNow.compareTo(remaining) > 0) {
            changeAmount = payNow.subtract(remaining);
            payNow = remaining;
        }

        Payment p = new Payment();
        p.setOrder(o);
        p.setAmount(payNow.doubleValue());
        p.setPaymentDate(LocalDate.now());
        p.setPaymentType(payNow.compareTo(grandTotal) >= 0 ? PaymentType.FULL : PaymentType.DEPOSIT);
        p.setMethod(PaymentMethod.CASH);
        p.setStatus(PaymentStatus.PAID);
        paymentRepo.save(p);

        BigDecimal newCollected = collected.add(payNow);
        BigDecimal newRemaining = grandTotal.subtract(newCollected).max(BigDecimal.ZERO);

        if (newRemaining.signum() == 0) {
            o.setPaymentStatus(PaymentStatus.PAID);
            o.setStatus(OrderStatus.PROCESSING);
        } else {
            o.setPaymentStatus(PaymentStatus.PROCESSING);
            o.setStatus(OrderStatus.PENDING);
        }
        orderRepo.save(o);

        CashPaymentResponse r = new CashPaymentResponse();
        r.orderId = o.getOrderId();
        r.paidNow = (req.amount == null) ? newCollected.subtract(collected) : req.amount;
        r.totalCollected = newCollected;
        r.grandTotal = grandTotal;
        r.remaining = newRemaining;
        r.changeAmount = changeAmount;
        r.orderStatus = o.getStatus().name();
        r.paymentStatus = o.getPaymentStatus().name();
        return r;
    }

    // ====== VNPay: TẠO LINK (style dự án mẫu) ======
    @Override
    @Transactional
    public VnPayLinkResponse createVnPayUrl(Long orderId) {
        if (orderId == null) throw new IllegalArgumentException("orderId is required");

        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Số tiền còn lại cần thanh toán
        BigDecimal grandTotal = BigDecimal.valueOf(o.getTotalPrice());
        BigDecimal collected = paymentRepo.findByOrder_OrderId(o.getOrderId())
                .stream().map(p -> BigDecimal.valueOf(p.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remaining = grandTotal.subtract(collected).max(BigDecimal.ZERO);

        VnPayLinkResponse res = new VnPayLinkResponse();
        res.orderId = o.getOrderId();
        res.amountToPay = remaining;

        if (remaining.signum() == 0) {
            res.paymentStatus = "PAID";
            res.vnpPaymentUrl = null;
            res.note = "Order already fully paid";
            return res;
        }

        // Tạo bản ghi Payment để lấy ID làm vnp_TxnRef
        Payment p = new Payment();
        p.setOrder(o);
        p.setAmount(remaining.doubleValue());
        p.setPaymentDate(LocalDate.now());
        p.setPaymentType(remaining.compareTo(grandTotal) >= 0 ? PaymentType.FULL : PaymentType.DEPOSIT);
        p.setMethod(PaymentMethod.VNPAY);
        p.setStatus(PaymentStatus.PROCESSING);
        paymentRepo.save(p);

        String txnRef = p.getPaymentId() + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // Tham số theo VNPay
        Map<String, String> vnp = new TreeMap<>();
        vnp.put("vnp_Version",  VNP_VERSION);
        vnp.put("vnp_Command",  VNP_COMMAND);
        vnp.put("vnp_TmnCode",  vnpTmnCode);
        vnp.put("vnp_Locale",   VNP_LOCALE);
        vnp.put("vnp_CurrCode", VNP_CURR);
        vnp.put("vnp_TxnRef",   txnRef);
        vnp.put("vnp_OrderInfo","Payment for order: " + o.getOrderId());
        vnp.put("vnp_OrderType",VNP_ORDTYPE);
        vnp.put("vnp_Amount",   remaining.multiply(new BigDecimal(100)).toBigInteger().toString());
        vnp.put("vnp_ReturnUrl",vnpReturnUrl);
        vnp.put("vnp_CreateDate", createDate);
        vnp.put("vnp_IpAddr",   vnpIp);
        // Expire (tuỳ chọn 15’)
        String expire = LocalDateTime.now().plusMinutes(15)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        vnp.put("vnp_ExpireDate", expire);

        // Ký HMAC SHA512
        String signData = buildSignData(vnp);
        String secureHash = hmacSHA512(vnpSecretKey, signData);
        vnp.put("vnp_SecureHash", secureHash);

        // Build URL
        String paymentUrl = buildPaymentUrl(vnpUrl, vnp);

        res.paymentStatus = "PROCESSING";
        res.vnpPaymentUrl = paymentUrl;
        res.note = "Redirect user to VNPay to complete payment";
        return res;
    }

    // ====== VNPay: RETURN ======
    @Override
    @Transactional
    public Map<String, String> handleVnPayReturn(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();

        try {
            String vnpSecureHash = params.get("vnp_SecureHash");
            // Tạo bản params để ký lại (trừ SecureHash)
            Map<String, String> sorted = new TreeMap<>();
            params.forEach((k, v) -> { if (!"vnp_SecureHash".equals(k)) sorted.put(k, v); });

            String signData = buildSignData(sorted);
            String computed  = hmacSHA512(vnpSecretKey, signData);
            if (!computed.equalsIgnoreCase(vnpSecureHash)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid checksum");
                return response;
            }

            String respCode = params.get("vnp_ResponseCode"); // "00" success
            String txnRef   = params.get("vnp_TxnRef");
            Long paymentId  = Long.valueOf(txnRef.split("-")[0]); // tách lại id

            Payment payment = paymentRepo.findById(paymentId)
                    .orElse(null);
            if (payment == null) {
                response.put("RspCode", "01");
                response.put("Message", "Payment not found");
                return response;
            }

            Order order = payment.getOrder();

            if (SUCCESS_CODE.equals(respCode)) {
                payment.setStatus(PaymentStatus.PAID);
                paymentRepo.save(payment);

                BigDecimal grandTotal = BigDecimal.valueOf(order.getTotalPrice());
                BigDecimal collected = paymentRepo.findByOrder_OrderId(order.getOrderId())
                        .stream().map(p -> BigDecimal.valueOf(p.getAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (collected.compareTo(grandTotal) >= 0) {
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.setStatus(OrderStatus.PROCESSING);
                } else {
                    order.setPaymentStatus(PaymentStatus.PROCESSING);
                    order.setStatus(OrderStatus.PENDING);
                }
                orderRepo.save(order);

                response.put("RspCode", SUCCESS_CODE);
                response.put("Message", "Payment success");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepo.save(payment);

                response.put("RspCode", respCode);
                response.put("Message", "Payment failed with code=" + respCode);
            }
            return response;

        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Error processing payment: " + e.getMessage());
            return response;
        }
    }

    // ===== Helpers (y hệt style mẫu) =====
    private String buildSignData(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .append('&');
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String buildPaymentUrl(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl).append('?');
        for (Map.Entry<String, String> e : params.entrySet()) {
            url.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .append('&');
        }
        if (url.length() > 0) url.deleteCharAt(url.length() - 1);
        return url.toString();
    }

    private String hmacSHA512(String secretKey, String data) {
        try {
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            hmacSha512.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = hmacSha512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot generate HMAC SHA512", e);
        }
    }
}
