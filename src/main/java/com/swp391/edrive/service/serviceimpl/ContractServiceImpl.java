package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.CustomerContractResponse;
import com.swp391.edrive.dto.response.ManufacturerContractResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.ContractStatus;
import com.swp391.edrive.mapper.contract.IContractMapper;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.ContractService;
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

    @Override
    public Object getAllContracts() {
        List<Contract> contracts = contractRepo.findAll();

        List<ManufacturerContractResponse> manufacturerContracts = new java.util.ArrayList<>();
        List<CustomerContractResponse> customerContracts = new java.util.ArrayList<>();

        for (Contract contract : contracts) {
            // Kiểm tra loại hợp đồng dựa vào customer
            Order order = contract.getOrder();
            boolean isCustomerContract = (order != null && order.getCustomer() != null);

            if (isCustomerContract) {
                customerContracts.add(mapper.toCustomerContractResponse(contract));
            } else {
                manufacturerContracts.add(mapper.toManufacturerContractResponse(contract));
            }
        }

        return com.swp391.edrive.dto.response.ContractListResponse.builder()
                .manufacturerContracts(manufacturerContracts)
                .customerContracts(customerContracts)
                .build();
    }

    @Override
    @Transactional
    public ManufacturerContractResponse create(ContractRequest req) {
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

        // Tạo contract với trạng thái ĐÃ_XÁC_NHẬN
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
                .status(ContractStatus.ĐÃ_XÁC_NHẬN)
                .build();

        Contract savedContract = contractRepo.save(c);

        ManufacturerContractResponse response = mapper.toManufacturerContractResponse(savedContract);
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
                .status(ContractStatus.CHỜ_DUYỆT)
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
    public CustomerContractResponse reviewContract(Long contractId, Boolean approved, String rejectionReason) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng với ID: " + contractId));

        // Kiểm tra trạng thái hiện tại
        if (contract.getStatus() != ContractStatus.CHỜ_DUYỆT) {
            throw new IllegalStateException("Chỉ có thể duyệt hợp đồng ở trạng thái CHỜ_DUYỆT");
        }

        if (approved) {
            // APPROVE: Chuyển sang ĐÃ_XÁC_NHẬN và xử lý inventory
            contract.setStatus(ContractStatus.ĐÃ_XÁC_NHẬN);
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
            // REJECT: Chuyển sang ĐÃ_TỪ_CHỐI
            contract.setStatus(ContractStatus.ĐÃ_TỪ_CHỐI);
            contract.setManufacturerNote(rejectionReason != null ? rejectionReason : "Đã từ chối");

            // Hoàn lại trạng thái payment của order về CHỜ_DUYỆT
            Order order = contract.getOrder();
            order.setPaymentStatus(com.swp391.edrive.enums.PaymentStatus.CHỜ_DUYỆT);
            orderRepo.save(order);
        }

        Contract savedContract = contractRepo.save(contract);

        // Build CustomerContractResponse
        Order order = savedContract.getOrder();
        Customer customer = order.getCustomer();
        Vehicle vehicle = order.getOrderItems().get(0).getVehicle();
        Dealer dealer = savedContract.getDealer();

        // Tính toán chi phí
        BigDecimal totalPrice = order.getTotalPrice();
        BigDecimal depositAmount = totalPrice
                .multiply(new BigDecimal("0.07"))
                .setScale(0, java.math.RoundingMode.HALF_UP);
        BigDecimal remainingAmount = totalPrice.subtract(depositAmount);

        return CustomerContractResponse.builder()
                .id(savedContract.getId())
                .contractCode(savedContract.getContractCode())
                .orderId(order.getOrderId())
                // Thông tin khách hàng
                .customerId(customer != null ? customer.getCustomerId() : null)
                .customerName(customer != null ? customer.getFullName() : null)
                .customerEmail(customer != null ? customer.getEmail() : null)
                .customerPhone(customer != null ? customer.getPhone() : null)
                .customerAddress(customer != null ? customer.getAddress() : null)
                // Thông tin đại lý
                .dealerId(dealer.getDealerId())
                .dealerName(dealer.getDealerName())
                .dealerManagerName(dealer.getContactPerson())
                .dealerPhone(dealer.getPhone())
                .dealerEmail(dealer.getDealerEmail())
                // Thông tin xe
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .colorName(vehicle.getColor() != null ? vehicle.getColor().getColorName() : null)
                // Chi phí
                .subtotal(order.getSubtotal())
                .discountAmount(order.getTotalDiscount())
                .vatAmount(order.getVatAmount())
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
    public ManufacturerContractResponse submitToManufacturer(Long contractId) {
        Contract c = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        if (c.getStatus() != ContractStatus.BẢN_NHÁP && c.getStatus() != ContractStatus.ĐÃ_TỪ_CHỐI) {
            throw new IllegalStateException("Chỉ có hợp đồng BẢN_NHÁP/ĐÃ_TỪ_CHỐI mới có thể gửi");
        }
        c.setStatus(ContractStatus.CHỜ_DUYỆT);
        return mapper.toManufacturerContractResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ManufacturerContractResponse approve(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));
        if (c.getStatus() != ContractStatus.CHỜ_DUYỆT) {
            throw new IllegalStateException("Chỉ có hợp đồng CHỜ_DUYỆT mới có thể phê duyệt");
        }
        c.setStatus(ContractStatus.ĐÃ_XÁC_NHẬN);
        c.setManufacturerNote(note);
        return mapper.toManufacturerContractResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ManufacturerContractResponse reject(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));
        if (c.getStatus() != ContractStatus.CHỜ_DUYỆT) {
            throw new IllegalStateException("Chỉ có hợp đồng CHỜ_DUYỆT mới có thể từ chối");
        }
        c.setStatus(ContractStatus.ĐÃ_TỪ_CHỐI);
        c.setManufacturerNote(note);
        return mapper.toManufacturerContractResponse(contractRepo.save(c));
    }

    @Override
    public Object getById(Long id) {
        Contract contract = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));

        // Kiểm tra loại hợp đồng dựa vào customer
        Order order = contract.getOrder();
        boolean isCustomerContract = (order != null && order.getCustomer() != null);

        if (isCustomerContract) {
            // Hợp đồng Đại lý ↔ Khách hàng
            return mapper.toCustomerContractResponse(contract);
        } else {
            // Hợp đồng Hãng ↔ Đại lý
            return mapper.toManufacturerContractResponse(contract);
        }
    }

    @Override
    public List<ManufacturerContractResponse> getByDealer(Long dealerId) {
        return contractRepo.findByDealer_DealerId(dealerId).stream()
                .map(mapper::toManufacturerContractResponse)
                .toList();
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
}

