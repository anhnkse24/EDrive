package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.ContractStatus;
import com.swp391.edrive.mapper.contract.IContractMapper;
import com.swp391.edrive.repository.ContractRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.ManufacturerRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.ContractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepo;
    private final DealerRepository dealerRepo;
    private final ManufacturerRepository manufacturerRepo;
    private final IContractMapper mapper;
    private final VehicleRepository vehicleRepo;

    @Override
    @Transactional
    public ContractResponse create(ContractRequest req) {
        Dealer dealer = dealerRepo.findById(req.getDealerId())
                .orElseThrow(() -> new EntityNotFoundException("Dealer not found"));

        Vehicle vehicle = vehicleRepo.findById(req.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        Manufacturer manufacturer = vehicle.getManufacturer();

        Contract c = Contract.builder()
                .dealer(dealer)
                .manufacturer(manufacturer)
                .vehicleModel(vehicle.getModelName())
                .vehicleVersion(vehicle.getVersion())
                .totalPrice(req.getTotalPrice())
                .discountRate(req.getDiscountRate())
                .terms(req.getTerms())
                .status(ContractStatus.DRAFT)
                .build();

        return mapper.toResponse(contractRepo.save(c));
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

}
