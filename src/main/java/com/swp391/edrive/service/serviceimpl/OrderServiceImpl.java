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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
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
    private final DiscountPolicyRepository discountPolicyRepo;

    @Value("${edrive.vat-rate:0.1}")
    private BigDecimal vatRate;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (order.getStatus() == OrderStatus.ĐÃ_GIAO) {
            throw new IllegalStateException("Đơn hàng đã giao không thể hủy");
        }
        if (order.getPaymentStatus() == PaymentStatus.ĐÃ_THANH_TOÁN) {
            throw new IllegalStateException("Đơn hàng đã thanh toán không thể hủy");
        }

        order.setStatus(OrderStatus.ĐÃ_HUỶ);
        order.setPaymentStatus(PaymentStatus.ĐÃ_HUỶ);
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
        res.setPaymentStatus(order.getPaymentStatus());

        if (order.getOrderItems() != null) {
            res.setOrderItems(
                    order.getOrderItems().stream().map(item -> {
                        var itemRes = new com.swp391.edrive.dto.response.OrderItemResponse();
                        itemRes.vehicleId = item.getVehicle().getVehicleId();
                        itemRes.vehicleName = item.getVehicle().getModelName();

                        // Thêm thông tin màu xe
                        if (item.getVehicle().getColor() != null) {
                            itemRes.colorName = item.getVehicle().getColor().getColorName();
                        }

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
        dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đại lý với id: " + dealerId));

        // lấy danh sách order theo dealer
        List<Order> orders = orderRepo.findByDealer_DealerId(dealerId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }
    @Override
    @Transactional
    public OrderSummaryResponse createOrder(OrderCreateRequest req, Long dealerId, User createdBy) {
        validate(req);  // Kiểm tra tính hợp lệ của dữ liệu đầu vào

        // Lấy thông tin Dealer từ dealerId
        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đại lý"));

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên
        order.setOrderDate(LocalDate.now());  // Ngày đặt hàng
        order.setDealer(dealer);  // Đặt dealer từ đối tượng đã tìm thấy
        order.setCreatedBy(createdBy);  // Lưu user tạo order
        order.setStatus(OrderStatus.CHỜ_DUYỆT);  // Trạng thái đơn hàng

        // Ensure paymentStatus CHỜ_DUYỆT set by entity PrePersist, but set explicitly for clarity
        order.setPaymentStatus(PaymentStatus.CHỜ_DUYỆT);

        order.setDesiredDeliveryDate(req.getDesiredDeliveryDate());  // Ngày giao hàng mong muốn
        order.setDeliveryAddress(req.getDeliveryAddress());  // Địa chỉ giao hàng
        order.setDeliveryNote(req.getDeliveryNote());  // Ghi chú giao hàng

        // Xử lý các item trong đơn hàng
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalSubtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : req.getOrderItems()) {
            Vehicle vehicle = vehicleRepo.findById(itemReq.getVehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe: " + itemReq.getVehicleId()));

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
            throw new IllegalArgumentException("Đơn hàng phải chứa ít nhất một sản phẩm");
        }

        for (OrderItemRequest item : req.getOrderItems()) {
            if (item.getVehicleId() == null) {
                throw new IllegalArgumentException("vehicleId là bắt buộc cho tất cả sản phẩm");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0 cho tất cả sản phẩm");
            }
        }

        if (req.getDesiredDeliveryDate() != null &&
                req.getDesiredDeliveryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày giao hàng mong muốn không thể là quá khứ");
        }
    }

    private void validateManufacturerInventory(Vehicle vehicle, Integer quantity) {
        ManufacturerInventory inventory = manufacturerInventoryRepo
                .findByVehicle_VehicleId(vehicle.getVehicleId())
                .orElseThrow(() -> new IllegalStateException(
                        "Xe không có sẵn trong kho nhà sản xuất: " + vehicle.getVehicleId()));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalStateException(
                    "Không đủ tồn kho cho xe " + vehicle.getModelName() +
                            ". Có sẵn: " + inventory.getQuantity() + ", Yêu cầu: " + quantity);
        }
    }

    private BigDecimal calculateDiscountRate(Integer quantity) {
        // Tìm chính sách chiết khấu từ database dựa trên số lượng
        return discountPolicyRepo.findByQuantityRange(quantity)
                .map(DiscountPolicy::getDiscountRate)
                .orElse(BigDecimal.ZERO); // Không có chiết khấu nếu không tìm thấy policy
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

            // Thêm thông tin màu xe
            if (item.getVehicle().getColor() != null) {
                itemResponse.colorName = item.getVehicle().getColor().getColorName();
            }

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

    @Override
    @Transactional
    public String uploadPaymentImage(String orderId, MultipartFile bill) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (bill.isEmpty()) {
            throw new IllegalArgumentException("Tệp hóa đơn không được để trống");
        }

        String originalFileName = bill.getOriginalFilename();
        if (originalFileName == null || (!originalFileName.toLowerCase().endsWith(".jpg")
                && !originalFileName.toLowerCase().endsWith(".jpeg")
                && !originalFileName.toLowerCase().endsWith(".png")
                && !originalFileName.toLowerCase().endsWith(".pdf"))) {
            throw new IllegalArgumentException("Định dạng tệp hóa đơn không hợp lệ. Chỉ chấp nhận JPG, JPEG, PNG hoặc PDF");
        }

        if (bill.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước tệp hóa đơn vượt quá giới hạn tối đa 10MB");
        }

        try {
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = "bill_" + orderId + "_" + UUID.randomUUID() + fileExtension;

            String uploadDirPath;
            if (uploadDir != null && !uploadDir.isEmpty() && !uploadDir.equals("uploads")) {
                uploadDirPath = uploadDir;
            } else {
                String userHome = System.getProperty("user.home");
                uploadDirPath = userHome + File.separator + "edrive_uploads" + File.separator + "bills";
            }

            File uploadDirFile = new File(uploadDirPath);

            if (!uploadDirFile.exists()) {
                boolean created = uploadDirFile.mkdirs();
                if (!created) {
                    throw new RuntimeException("Không thể tạo thư mục upload: " + uploadDirPath);
                }
            }

            File uploadFile = new File(uploadDirFile, uniqueFileName);
            bill.transferTo(uploadFile);

            order.setPaymentImage(uploadFile.getAbsolutePath());
            orderRepo.save(order);

            return "Tải lên hóa đơn thành công. Tệp: " + uploadFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải lên tệp hóa đơn: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getPaymentBillContent(String orderId) throws IOException {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        String paymentImagePath = order.getPaymentImage();
        if (paymentImagePath == null || paymentImagePath.isEmpty()) {
            throw new RuntimeException("Chưa có hóa đơn được tải lên cho đơn hàng này");
        }

        File file = new File(paymentImagePath);
        if (!file.exists()) {
            throw new RuntimeException("Không tìm thấy tệp hóa đơn tại: " + paymentImagePath);
        }

        return Files.readAllBytes(file.toPath());
    }

    @Override
    public String getPaymentBillContentType(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        String paymentImagePath = order.getPaymentImage();
        if (paymentImagePath == null || paymentImagePath.isEmpty()) {
            throw new RuntimeException("Chưa có hóa đơn được tải lên cho đơn hàng này");
        }

        String lowerPath = paymentImagePath.toLowerCase();
        if (lowerPath.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }

    @Override
    public String getPaymentBillFileName(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        String paymentImagePath = order.getPaymentImage();
        if (paymentImagePath == null || paymentImagePath.isEmpty()) {
            throw new RuntimeException("Chưa có hóa đơn được tải lên cho đơn hàng này");
        }

        File file = new File(paymentImagePath);
        return file.getName();
    }

    @Override
    @Transactional
    public OrderResponse markOrderAsPaid(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getPaymentImage() == null || order.getPaymentImage().isEmpty()) {
            throw new IllegalStateException("Không thể đánh dấu là ĐÃ THANH TOÁN: chưa có hóa đơn được tải lên");
        }

        if (order.getPaymentStatus() == PaymentStatus.ĐÃ_THANH_TOÁN) {
            throw new IllegalStateException("Đơn hàng đã được đánh dấu là ĐÃ THANH TOÁN");
        }

        // Chỉ thay đổi PaymentStatus sang PAID, OrderStatus giữ nguyên
        order.setPaymentStatus(PaymentStatus.ĐÃ_THANH_TOÁN);
        order.setStatus(OrderStatus.ĐÃ_XÁC_NHẬN);
        orderRepo.save(order);

        return mapToOrderResponse(order);
    }
}