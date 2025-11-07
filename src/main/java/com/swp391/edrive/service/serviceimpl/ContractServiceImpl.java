package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractResponse;
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

        // Tạo contract với trạng thái ĐÃ_XÁC_NHẬN
        Vehicle vehicle = order.getOrderItems().get(0).getVehicle();

        Contract c = Contract.builder()
                .order(order)
                .dealer(dealer)
                .manufacturer(manufacturer)
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .totalPrice(totalPrice)
                .discountRate(discountRate)
                .terms(req.getTerms())
                .status(ContractStatus.ĐÃ_XÁC_NHẬN)
                .build();

        Contract savedContract = contractRepo.save(c);

        ContractResponse response = mapper.toResponse(savedContract);
        response.setSubtotal(subtotal);
        response.setVatAmount(vatAmount);

        return response;
    }

    @Override
    @Transactional
    public ContractResponse submitToManufacturer(Long contractId) {
        Contract c = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        if (c.getStatus() != ContractStatus.BẢN_NHÁP && c.getStatus() != ContractStatus.ĐÃ_TỪ_CHỐI) {
            throw new IllegalStateException("Only BẢN_NHÁP/ĐÃ_TỪ_CHỐI contracts can be submitted");
        }
        c.setStatus(ContractStatus.CHỜ_DUYỆT);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ContractResponse approve(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        if (c.getStatus() != ContractStatus.CHỜ_DUYỆT) {
            throw new IllegalStateException("Only CHỜ_DUYỆT contracts can be rejected");
        }
        c.setStatus(ContractStatus.ĐÃ_XÁC_NHẬN);
        c.setManufacturerNote(note);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ContractResponse reject(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        if (c.getStatus() != ContractStatus.CHỜ_DUYỆT) {
            throw new IllegalStateException("Only CHỜ_DUYỆT contracts can be rejected");
        }
       c.setStatus(ContractStatus.ĐÃ_TỪ_CHỐI);
        c.setManufacturerNote(note);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    public ContractResponse getById(Long id) {
        return mapper.toResponse(contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found")));
    }

    @Override
    public List<ContractResponse> getByDealer(Long dealerId) {
        return contractRepo.findByDealer_DealerId(dealerId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ContractFileResponse uploadPdf(Long contractId, MultipartFile file) {
        Contract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
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

            return ContractFileResponse.builder()
                    .contractId(contractId)
                    .pdfFilename(filename)
                    .uploadedAt(contract.getPdfUploadedAt())
                    .downloadUrl(fileUrl)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Upload PDF failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Contract findEntityById(Long id) {
        return contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
    }
}
