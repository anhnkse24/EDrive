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
    private final ManufacturerRepository manufacturerRepo;
    private final IContractMapper mapper;
    private final VehicleRepository vehicleRepo;
    private final OrderRepository orderRepo;


    @Override
    public List<ContractResponse> getAllContracts() {
        List<Contract> contracts = contractRepo.findAll(); // Lấy tất cả hợp đồng từ database
        return contracts.stream()
                .map(mapper::toResponse) // Chuyển đổi từ Contract sang ContractResponse
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContractResponse create(ContractRequest req) {
        Dealer dealer = dealerRepo.findById(req.getDealerId())
                .orElseThrow(() -> new EntityNotFoundException("Dealer not found"));

        Order order = orderRepo.findById(req.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        Vehicle vehicle = order.getOrderItems().get(0).getVehicle();  // Giả sử mỗi hợp đồng chỉ có một loại xe
        BigDecimal totalPrice = order.getTotalPrice();  // Tổng giá của đơn hàng
        BigDecimal discountRate = order.getTotalDiscount();  // Chiết khấu từ đơn hàng
        BigDecimal subtotal = order.getSubtotal();  // Giá chưa giảm (subtotal)
        BigDecimal vatAmount = order.getVatAmount();  // Lấy VAT từ đơn hàng

        Manufacturer manufacturer = vehicle.getManufacturer();  // Lấy thông tin nhà sản xuất từ xe

        Contract c = Contract.builder()
                .dealer(dealer)
                .manufacturer(manufacturer)
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .totalPrice(totalPrice)
                .discountRate(discountRate)
                .terms(req.getTerms())
                .status(ContractStatus.DRAFT)  // Trạng thái hợp đồng là draft khi mới tạo
                .build();

// Lưu hợp đồng vào cơ sở dữ liệu
        Contract savedContract = contractRepo.save(c);

// Trả về phản hồi hợp đồng
        ContractResponse response = mapper.toResponse(savedContract);
        response.setSubtotal(subtotal);  // Trả về giá chưa giảm (subtotal)
        response.setVatAmount(vatAmount);  // Trả về phí VAT tính từ đơn hàng

        return response;
    }

    @Override
    @Transactional
    public ContractResponse submitToManufacturer(Long contractId) {
        Contract c = contractRepo.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        if (c.getStatus() != ContractStatus.DRAFT && c.getStatus() != ContractStatus.REJECTED) {
            throw new IllegalStateException("Only DRAFT/REJECTED contracts can be submitted");
        }
        c.setStatus(ContractStatus.PENDING_MANUFACTURER);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ContractResponse approve(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        if (c.getStatus() != ContractStatus.PENDING_MANUFACTURER) {
            throw new IllegalStateException("Only PENDING_MANUFACTURER can be approved");
        }
        c.setStatus(ContractStatus.APPROVED);
        c.setManufacturerNote(note);
        return mapper.toResponse(contractRepo.save(c));
    }

    @Override
    @Transactional
    public ContractResponse reject(Long id, String note) {
        Contract c = contractRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        if (c.getStatus() != ContractStatus.PENDING_MANUFACTURER) {
            throw new IllegalStateException("Only PENDING_MANUFACTURER can be rejected");
        }
        c.setStatus(ContractStatus.REJECTED);
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
