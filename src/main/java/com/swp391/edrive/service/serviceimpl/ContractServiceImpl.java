package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.*;
import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractListResponse;
import com.swp391.edrive.dto.response.CustomerContractResponse;
import com.swp391.edrive.dto.response.ManufacturerContractResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.ContractStatus;
import com.swp391.edrive.mapper.contract.IContractMapper;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.ContractService;
import com.swp391.edrive.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepo;
    private final DealerRepository dealerRepo;
    private final IContractMapper mapper;
    private final OrderRepository orderRepo;
    private final ManufacturerInventoryRepository manufacturerInventoryRepo;
    private final DealerInventoryRepository dealerInventoryRepo;
    private final NotificationService notificationService;

    @Override
    public List<ContractResponse> getAllContracts() {
        List<Contract> contracts = contractRepo.findAll();
        return contracts.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContractResponse create(ContractRequest req) {
        Dealer dealer = dealerRepo.findById(req.getDealerId())
                .orElseThrow(() -> new EntityNotFoundException("Dealer not found"));

        Order order = orderRepo.findById(req.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        Manufacturer manufacturer = null;
        BigDecimal totalPrice = order.getTotalPrice();
        BigDecimal discountRate = order.getTotalDiscount();
        BigDecimal subtotal = order.getSubtotal();
        BigDecimal vatAmount = order.getVatAmount();

        // Process all items in order: transfer inventory & create contract
        for (OrderItem orderItem : order.getOrderItems()) {
            Vehicle vehicle = orderItem.getVehicle();
            Integer quantity = orderItem.getQuantity();

            if (manufacturer == null) {
                manufacturer = vehicle.getManufacturer();
            }

            // Trừ từ kho hãng
            ManufacturerInventory manufacturerInventory = manufacturerInventoryRepo
                    .findByVehicle_VehicleId(vehicle.getVehicleId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Manufacturer inventory not found for vehicle: " + vehicle.getVehicleId()));

            if (manufacturerInventory.getQuantity() < quantity) {
                throw new IllegalStateException(
                        "Insufficient manufacturer inventory for vehicle: " + vehicle.getModelName() +
                                ". Available: " + manufacturerInventory.getQuantity() + ", Requested: " + quantity);
            }
            manufacturerInventory.setQuantity(manufacturerInventory.getQuantity() - quantity);
            manufacturerInventory.setLastUpdated(LocalDateTime.now());
            manufacturerInventoryRepo.save(manufacturerInventory);

            // Cộng vào kho đại lý
            DealerInventory dealerInventory = dealerInventoryRepo
                    .findByDealer_DealerIdAndVehicle_VehicleId(dealer.getDealerId(), vehicle.getVehicleId())
                    .orElse(null);

            if (dealerInventory != null) {
                dealerInventory.setQuantity(dealerInventory.getQuantity() + quantity);
                dealerInventory.setLastUpdated(LocalDateTime.now());
            } else {
                dealerInventory = DealerInventory.builder()
                        .dealer(dealer)
                        .vehicle(vehicle)
                        .quantity(quantity)
                        .lastUpdated(LocalDateTime.now())
                        .build();
            }

            dealerInventoryRepo.save(dealerInventory);
        }

        // Tạo contract với trạng thái DRAFT
        Vehicle vehicle = order.getOrderItems().get(0).getVehicle();
        String colorName = (vehicle.getColor() != null) ? vehicle.getColor().getColorName() : null;

        Contract c = Contract.builder()
                .order(order)
                .dealer(dealer)
                .manufacturer(manufacturer)
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .colorName(colorName)
                .totalPrice(totalPrice)
                .discountRate(discountRate)
                .terms(req.getTerms())
                .status(ContractStatus.DRAFT)
                .build();

        Contract savedContract = contractRepo.save(c);

        ContractResponse response = mapper.toResponse(savedContract);
        response.setSubtotal(subtotal);
        response.setVatAmount(vatAmount);

        return response;
    }

    @Override
    @Transactional
    public CustomerContractResponse createContractFromOrder(String orderId) {
        // Lấy Order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra payment status (cho phép CHỜ_DUYỆT - dành cho khách hàng đã cọc)
        if (order.getPaymentStatus() != com.swp391.edrive.enums.PaymentStatus.CHỜ_DUYỆT) {
            throw new IllegalStateException("Chỉ có thể tạo hợp đồng cho đơn hàng đang chờ duyệt (đã cọc)");
        }

        // Kiểm tra đã có hợp đồng chưa
        if (contractRepo.existsByOrder_OrderId(orderId)) {
            throw new IllegalStateException("Đơn hàng này đã có hợp đồng");
        }

        Dealer dealer = order.getDealer();
        if (dealer == null) {
            throw new IllegalStateException("Đơn hàng không có thông tin đại lý");
        }

        // Lấy thông tin từ order
        BigDecimal totalPrice = order.getTotalPrice();
        BigDecimal discountAmount = order.getTotalDiscount();
        BigDecimal subtotal = order.getSubtotal();
        BigDecimal vatAmount = order.getVatAmount();

        // Tính tiền cọc 7%
        BigDecimal depositAmount = totalPrice
                .multiply(new java.math.BigDecimal("0.07"))
                .setScale(0, java.math.RoundingMode.HALF_UP);

        // Tính số tiền còn lại
        BigDecimal remainingAmount = totalPrice.subtract(depositAmount);

        // Lấy manufacturer và thông tin xe từ vehicle
        Vehicle vehicle = order.getOrderItems().get(0).getVehicle();
        Manufacturer manufacturer = vehicle.getManufacturer();
        String colorName = (vehicle.getColor() != null) ? vehicle.getColor().getColorName() : null;

        // Lấy thông tin khách hàng từ Order
        Customer customer = order.getCustomer();
        String customerName = null;
        String customerEmail = null;
        String customerPhone = null;
        String customerAddress = null;
        Long customerId = null;

        if (customer != null) {
            customerId = customer.getCustomerId();
            customerName = customer.getFullName();
            customerEmail = customer.getEmail();
            customerPhone = customer.getPhone();
            customerAddress = customer.getAddress();
        }

        // Lấy tên quản lý đại lý từ dealer.contactPerson
        String dealerManagerName = dealer.getContactPerson();
        String dealerPhone = dealer.getPhone();
        String dealerEmail = dealer.getDealerEmail();

        // Tạo hợp đồng với trạng thái CHỜ_DUYỆT
        Contract contract = Contract.builder()
                .order(order)
                .dealer(dealer)
                .manufacturer(manufacturer)
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .colorName(colorName)
                .totalPrice(totalPrice)
                .discountRate(discountAmount)
                .terms("Điều khoản hợp đồng mặc định")
                .status(ContractStatus.DRAFT)
                .build();

        Contract savedContract = contractRepo.save(contract);

        // Cập nhật payment status của order thành ĐÃ_CỌC (khách hàng đã cọc 7%)
        order.setPaymentStatus(com.swp391.edrive.enums.PaymentStatus.ĐÃ_CỌC);
        orderRepo.save(order);

        // Tạo CustomerContractResponse với đầy đủ thông tin
        return CustomerContractResponse.builder()
                .id(savedContract.getId())
                .contractCode(savedContract.getContractCode())
                .orderId(order.getOrderId())
                // Thông tin khách hàng
                .customerId(customerId)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .customerAddress(customerAddress)
                // Thông tin đại lý
                .dealerId(dealer.getDealerId())
                .dealerName(dealer.getDealerName())
                .dealerManagerName(dealerManagerName)
                .dealerPhone(dealerPhone)
                .dealerEmail(dealerEmail)
                // Thông tin xe
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .colorName(colorName)
                // Chi phí chi tiết
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .vatAmount(vatAmount)
                .totalPrice(totalPrice)
                .depositAmount(depositAmount)
                .remainingAmount(remainingAmount)
                // Thông tin hợp đồng
                .status(savedContract.getStatus().name())
                .terms(savedContract.getTerms())
                .createdAt(savedContract.getCreatedAt())
                .updatedAt(savedContract.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public ContractResponse reviewContract(Long contractId, Boolean approved, String rejectionReason) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng với ID: " + contractId));

        // Kiểm tra trạng thái hiện tại
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể duyệt hợp đồng ở trạng thái DRAFT");
        }

        if (approved) {
            // APPROVE: Giữ nguyên DRAFT (sẵn sàng cho hãng ký)
            contract.setStatus(ContractStatus.DRAFT);
            contract.setManufacturerNote("Đã phê duyệt");

            Order order = contract.getOrder();
            Dealer dealer = contract.getDealer();

            // Xử lý inventory cho tất cả items trong order
            for (OrderItem orderItem : order.getOrderItems()) {
                Vehicle vehicle = orderItem.getVehicle();
                Integer quantity = orderItem.getQuantity();

                // Trừ từ kho hãng
                ManufacturerInventory manufacturerInventory = manufacturerInventoryRepo
                        .findByVehicle_VehicleId(vehicle.getVehicleId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Không tìm thấy tồn kho hãng cho xe: " + vehicle.getVehicleId()));

                if (manufacturerInventory.getQuantity() < quantity) {
                    throw new IllegalStateException(
                            "Không đủ tồn kho cho xe " + vehicle.getModelName() +
                                    ". Có sẵn: " + manufacturerInventory.getQuantity() +
                                    ", Yêu cầu: " + quantity);
                }

                manufacturerInventory.setQuantity(manufacturerInventory.getQuantity() - quantity);
                manufacturerInventory.setLastUpdated(LocalDateTime.now());
                manufacturerInventoryRepo.save(manufacturerInventory);

                // Cộng vào kho đại lý
                DealerInventory dealerInventory = dealerInventoryRepo
                        .findByDealer_DealerIdAndVehicle_VehicleId(dealer.getDealerId(), vehicle.getVehicleId())
                        .orElse(null);

                if (dealerInventory != null) {
                    dealerInventory.setQuantity(dealerInventory.getQuantity() + quantity);
                    dealerInventory.setLastUpdated(LocalDateTime.now());
                } else {
                    dealerInventory = DealerInventory.builder()
                            .dealer(dealer)
                            .vehicle(vehicle)
                            .quantity(quantity)
                            .lastUpdated(LocalDateTime.now())
                            .build();
                }

                dealerInventoryRepo.save(dealerInventory);
            }
        } else {
            // REJECT: Chuyển sang REJECTED
            contract.setStatus(ContractStatus.REJECTED);
            contract.setManufacturerNote(rejectionReason != null ? rejectionReason : "Đã từ chối");

            // Hoàn lại trạng thái payment của order về ĐÃ_THANH_TOÁN
            Order order = contract.getOrder();
            order.setPaymentStatus(com.swp391.edrive.enums.PaymentStatus.ĐÃ_THANH_TOÁN);
            orderRepo.save(order);
        }

        Contract savedContract = contractRepo.save(contract);
        return mapper.toResponse(savedContract);
    }

    @Override
    @Transactional
    public ContractResponse submitToManufacturer(Long contractId) {
        Contract c = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        if (c.getStatus() != ContractStatus.DRAFT && c.getStatus() != ContractStatus.REJECTED) {
            throw new IllegalStateException("Chỉ có hợp đồng DRAFT/REJECTED mới có thể gửi");
        }
        c.setStatus(ContractStatus.DRAFT);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ContractResponse approve(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));
        if (c.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có hợp đồng DRAFT mới có thể phê duyệt");
        }
        c.setStatus(ContractStatus.DRAFT);
        c.setManufacturerNote(note);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ContractResponse reject(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));
        if (c.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có hợp đồng DRAFT mới có thể từ chối");
        }
       c.setStatus(ContractStatus.REJECTED);
        c.setManufacturerNote(note);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    public List<ContractResponse> getByDealer(Long dealerId) {
        return contractRepo.findByDealer_DealerId(dealerId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ContractFileResponse uploadPdf(Long contractId, MultipartFile file) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được để trống");
        }
        long maxFileSize = 10 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("Kích thước tệp vượt quá giới hạn cho phép (tối đa 10MB).");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Chỉ được phép tải lên tệp định dạng PDF (.pdf).");
        }

        try {
            String uploadDir = "uploads/contracts/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = "contract_" + contractId + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            contract.setPdfFilename(filename);
            contract.setPdfUploadedAt(LocalDateTime.now());
            contractRepo.save(contract);

            notificationService.createNotificationForUploadedContract(contract);

            String fileUrl = "http://localhost:8080/uploads/contracts/" + filename;
            Dealer dealer = contract.getDealer();


            return ContractFileResponse.builder()
                    .contractId(contractId)
                    .contactName(dealer != null ? dealer.getContactPerson() : null)
                    .contactPhone(dealer != null ? dealer.getPhone() : null)
                    .pdfFilename(filename)
                    .uploadedAt(contract.getPdfUploadedAt())
                    .downloadUrl(fileUrl)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Tải lên PDF thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public Contract findEntityById(Long id) {
        return contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));
    }

    @Override
    @Transactional
    public ContractResponse dealerSign(Long contractId, String signatureData) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        // Kiểm tra trạng thái: cho phép ký khi SIGNING (hãng đã ký rồi)
        if (contract.getStatus() != ContractStatus.SIGNING) {
            throw new IllegalStateException("Hợp đồng không ở trạng thái SIGNING. Hãng phải ký trước.");
        }

        // Kiểm tra hãng đã ký chưa
        if (contract.getManufacturerSignedAt() == null) {
            throw new IllegalStateException("Hãng chưa ký hợp đồng");
        }

        contract.setDealerSignature(signatureData);
        contract.setDealerSignedAt(LocalDateTime.now());

        // Đại lý ký xong -> cả 2 đã ký đầy đủ -> ACTIVE
        contract.setStatus(ContractStatus.ACTIVE);

        return mapper.toResponse(contractRepo.save(contract));
    }

    @Override
    @Transactional
    public ContractResponse manufacturerSign(Long contractId, String signatureData) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        // Kiểm tra trạng thái: cho phép ký khi DRAFT hoặc SIGNING
        if (contract.getStatus() != ContractStatus.DRAFT
            && contract.getStatus() != ContractStatus.SIGNING) {
            throw new IllegalStateException("Hợp đồng không ở trạng thái cho phép hãng ký");
        }

        contract.setManufacturerSignature(signatureData);
        contract.setManufacturerSignedAt(LocalDateTime.now());

        // Nếu đại lý đã ký rồi -> cả 2 đã ký đầy đủ
        if (contract.getDealerSignedAt() != null) {
            contract.setStatus(ContractStatus.ACTIVE);
        } else {
            // Hãng ký xong, chuyển sang SIGNING (chờ đại lý)
            contract.setStatus(ContractStatus.SIGNING);
        }

        return mapper.toResponse(contractRepo.save(contract));
    }

    @Override
    @Transactional
    public ContractFileResponse uploadPaymentReceipt(Long contractId, MultipartFile file) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        // Chỉ cho phép upload biên lai khi đã ký đầy đủ (status = ACTIVE)
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Hợp đồng chưa được ký đầy đủ. Không thể upload biên lai thanh toán.");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được để trống");
        }

        long maxFileSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("Kích thước tệp vượt quá giới hạn cho phép (tối đa 10MB).");
        }

        try {
            String uploadDir = "uploads/payment-receipts/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = "receipt_" + contractId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            contract.setPaymentReceiptFilename(filename);
            contract.setPaymentReceiptUrl("/uploads/payment-receipts/" + filename);
            contract.setPaymentReceiptUploadedAt(LocalDateTime.now());
            // Status stays ACTIVE - no change on receipt upload

            contractRepo.save(contract);

            return ContractFileResponse.builder()
                    .contractId(contractId)
                    .pdfFilename(filename)
                    .uploadedAt(contract.getPaymentReceiptUploadedAt())
                    .downloadUrl("http://localhost:8080" + contract.getPaymentReceiptUrl())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Tải lên biên lai thanh toán thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ContractResponse verifyPayment(Long contractId, String verifiedBy) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Hợp đồng phải ở trạng thái ACTIVE để xác nhận thanh toán");
        }

        if (contract.getPaymentReceiptUrl() == null) {
            throw new IllegalStateException("Chưa có biên lai thanh toán để xác nhận");
        }

        contract.setPaymentVerifiedAt(LocalDateTime.now());
        contract.setPaymentVerifiedBy(verifiedBy);
        // Status stays ACTIVE - verification is just marking payment as confirmed

        return mapper.toResponse(contractRepo.save(contract));
    }

    @Override
    @Transactional
    public ContractResponse approveDelivery(Long contractId) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Hợp đồng phải ở trạng thái ACTIVE để duyệt giao xe");
        }

        if (contract.getPaymentVerifiedAt() == null) {
            throw new IllegalStateException("Phải xác nhận thanh toán trước khi duyệt giao xe");
        }

        // Status stays ACTIVE - delivery approval doesn't change contract status
        // Could add a deliveryApprovedAt field if needed

        return mapper.toResponse(contractRepo.save(contract));
    }

    @Override
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract = findEntityById(contractId);
        Order order = contract.getOrder();

        // Build buyer info
        String buyerName = null;
        String buyerPhone = null;
        String buyerAddress = null;

        if (order != null && order.getCreatedBy() != null) {
            User user = order.getCreatedBy();
            buyerName = user.getFullName();
        }

        ContractDetailResponse.BuyerInfo buyerInfo = ContractDetailResponse.BuyerInfo.builder()
                .name(buyerName)
                .phone(buyerPhone)
                .address(buyerAddress)
                .build();

        // Build dealer info
        Dealer dealer = contract.getDealer();
        ContractDetailResponse.DealerInfo dealerInfo = ContractDetailResponse.DealerInfo.builder()
                .id(dealer != null ? dealer.getDealerId() : null)
                .name(dealer != null ? dealer.getDealerName() : null)
                .phone(dealer != null ? dealer.getPhone() : null)
                .address(dealer != null ? buildDealerAddress(dealer) : null)
                .representative(dealer != null ? dealer.getContactPerson() : null)
                .signatureData(contract.getDealerSignature())
                .signedAt(contract.getDealerSignedAt())
                .build();

        // Build manufacturer info
        Manufacturer manufacturer = contract.getManufacturer();
        ContractDetailResponse.ManufacturerInfo manufacturerInfo = ContractDetailResponse.ManufacturerInfo.builder()
                .name(manufacturer != null ? manufacturer.getManufacturerName() : "E-DRIVE VIETNAM")
                .address(manufacturer != null ? manufacturer.getAddress() : "123 Đường Điện Biên Phủ, Quận 1, TP.HCM")
                .phone(manufacturer != null ? manufacturer.getContactPersonPhone() : "(0123) 456 789")
                .taxCode("0123456789")
                .signatureData(contract.getManufacturerSignature())
                .signedAt(contract.getManufacturerSignedAt())
                .build();

        // Build pricing info
        BigDecimal vatAmount = order != null ? order.getVatAmount() : BigDecimal.ZERO;
        BigDecimal discount = order != null ? order.getTotalDiscount() : BigDecimal.ZERO;

        ContractDetailResponse.PricingInfo pricingInfo = ContractDetailResponse.PricingInfo.builder()
                .subtotal(order != null ? order.getSubtotal() : contract.getTotalPrice())
                .discount(discount)
                .taxPercent(10)
                .total(contract.getTotalPrice())
                .build();

        return ContractDetailResponse.builder()
                .id(contract.getId())
                .orderId(order != null ? order.getOrderId() : null)
                .status(contract.getStatus().name())
                .buyer(buyerInfo)
                .dealer(dealerInfo)
                .manufacturer(manufacturerInfo)
                .pricing(pricingInfo)
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    @Override
    public OrderDetailResponse getOrderDetail(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));

        // Build dealer info
        OrderDetailResponse.DealerInfo dealerInfo = OrderDetailResponse.DealerInfo.builder()
                .id(order.getDealer() != null ? order.getDealer().getDealerId() : null)
                .name(order.getDealer() != null ? order.getDealer().getDealerName() : null)
                .build();

        // Build customer info
        String customerName = null;
        String customerPhone = null;
        String customerAddress = order.getDeliveryAddress();

        if (order.getCreatedBy() != null) {
            customerName = order.getCreatedBy().getFullName();
        }

        OrderDetailResponse.CustomerInfo customerInfo = OrderDetailResponse.CustomerInfo.builder()
                .name(customerName)
                .phone(customerPhone)
                .address(customerAddress)
                .build();

        // Build order items
        List<OrderDetailResponse.OrderItemInfo> orderItems = order.getOrderItems().stream()
                .map(item -> {
                    Vehicle vehicle = item.getVehicle();
                    BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    BigDecimal itemDiscount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
                    BigDecimal itemTotal = itemSubtotal.subtract(itemDiscount);

                    return OrderDetailResponse.OrderItemInfo.builder()
                            .vehicleName(vehicle.getModelName())
                            .color(vehicle.getColor() != null ? vehicle.getColor().getColorName() : null)
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .itemSubtotal(itemSubtotal)
                            .itemDiscount(itemDiscount)
                            .itemTotal(itemTotal)
                            .build();
                })
                .collect(Collectors.toList());

        // Build money info
        BigDecimal paidTotal = BigDecimal.ZERO; // TODO: Calculate from payments
        BigDecimal remaining = order.getTotalPrice().subtract(paidTotal);

        OrderDetailResponse.MoneyInfo moneyInfo = OrderDetailResponse.MoneyInfo.builder()
                .subtotal(order.getSubtotal())
                .discount(order.getTotalDiscount())
                .taxPercent(10)
                .fees(BigDecimal.ZERO)
                .total(order.getTotalPrice())
                .paidTotal(paidTotal)
                .remaining(remaining)
                .build();

        return OrderDetailResponse.builder()
                .id(order.getOrderId())
                .code(order.getOrderId())
                .orderDate(order.getOrderDate())
                .desiredDeliveryDate(order.getDesiredDeliveryDate())
                .dealer(dealerInfo)
                .customer(customerInfo)
                .orderItems(orderItems)
                .money(moneyInfo)
                .build();
    }

    @Override
    @Transactional
    public SignatureResponse saveManufacturerSignature(Long contractId, String signatureData) {
        Contract contract = findEntityById(contractId);

        contract.setManufacturerSignature(signatureData);
        contract.setManufacturerSignedAt(LocalDateTime.now());

        // Update status to SIGNING if not already
        if (contract.getStatus() == ContractStatus.DRAFT) {
            contract.setStatus(ContractStatus.SIGNING);
        } else if (contract.getDealerSignedAt() != null) {
            contract.setStatus(ContractStatus.ACTIVE);
        }

        contractRepo.save(contract);

        Manufacturer manufacturer = contract.getManufacturer();

        return SignatureResponse.builder()
                .id(contract.getId())
                .orderId(contract.getOrder() != null ? contract.getOrder().getOrderId() : null)
                .status(contract.getStatus().name())
                .manufacturer(SignatureResponse.ManufacturerSignature.builder()
                        .name(manufacturer != null ? manufacturer.getManufacturerName() : "E-DRIVE VIETNAM")
                        .signatureData(signatureData)
                        .signedAt(contract.getManufacturerSignedAt())
                        .build())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public PdfUploadResponse uploadContractPdf(Long contractId, MultipartFile file, String fileName) {
        Contract contract = findEntityById(contractId);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được để trống");
        }

        // Validate file size (5MB limit)
        long maxFileSize = 5 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds limit (5MB)");
        }

        // Validate file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        try {
            String uploadDir = "uploads/contracts/";
            Files.createDirectories(Paths.get(uploadDir));

            String finalFileName = fileName != null ? fileName : "contract_" + contractId + "_" + System.currentTimeMillis() + ".pdf";
            Path filePath = Paths.get(uploadDir, finalFileName);
            Files.write(filePath, file.getBytes());

            String pdfUrl = "http://localhost:8080/uploads/contracts/" + finalFileName;
            contract.setPdfFilename(finalFileName);
            contract.setPdfUrl(pdfUrl);
            contract.setPdfUploadedAt(LocalDateTime.now());
            // Status không đổi - chỉ update PDF URL (có thể upload lại nhiều lần)
            contractRepo.save(contract);

            return PdfUploadResponse.builder()
                    .success(true)
                    .message("PDF uploaded successfully")
                    .pdfUrl(pdfUrl)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("PDF upload failed: " + e.getMessage(), e);
        }
    }

    private String buildDealerAddress(Dealer dealer) {
        StringBuilder address = new StringBuilder();
        if (dealer.getHouseNumberAndStreet() != null) {
            address.append(dealer.getHouseNumberAndStreet());
        }
        if (dealer.getWardOrCommune() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(dealer.getWardOrCommune());
        }
        if (dealer.getDistrict() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(dealer.getDistrict());
        }
        if (dealer.getProvinceOrCity() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(dealer.getProvinceOrCity());
        }
        return address.toString();
    }
}
