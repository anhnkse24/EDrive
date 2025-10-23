package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CreateContractFromQuotationRequest;
import com.swp391.edrive.dto.request.SignContractRequest;
import com.swp391.edrive.dto.request.UpdateContractTermsRequest;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.entity.Contract;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Quotation;
import com.swp391.edrive.enums.ContractStatus;
import com.swp391.edrive.enums.QuotationStatus;
import com.swp391.edrive.repository.ContractRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepo;
    private final QuotationRepository quotationRepo;
    private final DealerRepository dealerRepo;

    @Override
    public ContractResponse createFromQuotation(CreateContractFromQuotationRequest req) {
        Quotation q = quotationRepo.findById(req.quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        // Only from APPROVED quotation
        if (q.getStatus() != QuotationStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED quotation can create a contract");
        }
        // If quotation has validUntil and already expired today, block
        if (q.getValidUntil() != null && q.getValidUntil().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Quotation is expired");
        }
        // Only one contract per quotation
        if (contractRepo.existsByQuotation_Id(q.getId())) {
            throw new IllegalStateException("Contract already exists for this quotation");
        }

        // Dealer: default from quotation; allow override but must match (để tránh lệch luồng)
        Dealer dealer = q.getDealer();
        if (req.dealerId != null && !req.dealerId.equals(dealer.getDealerId())) {
            // Cho phép override cứng nếu bạn muốn, còn mặc định chặn:
            throw new IllegalArgumentException("Dealer mismatch with quotation");
            // Dealer dealerOverride = dealerRepo.findById(req.dealerId)
            //        .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));
            // dealer = dealerOverride;
        }

        Contract c = new Contract();
        c.setDealer(dealer);
        c.setQuotation(q);
        c.setStatus(ContractStatus.DRAFT);
        c.setContractValue(q.getGrandTotal()); // snapshot value
        c.setTerms(req.terms);

        Contract saved = contractRepo.save(c);
        return toResponse(saved);
    }

    @Override
    public ContractResponse sign(Long contractId, SignContractRequest req) {
        Contract c = getEntity(contractId);

        if (c.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT contract can be signed");
        }
        // Optional: enforce quotation still valid at signing time
        var q = c.getQuotation();
        if (q.getValidUntil() != null && q.getValidUntil().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Quotation expired before signing");
        }

        c.setStatus(ContractStatus.ACTIVE);
        c.setSignedAt(req.signedAt != null ? req.signedAt : LocalDateTime.now());
        return toResponse(c);
    }

    @Override
    public ContractResponse complete(Long contractId) {
        Contract c = getEntity(contractId);
        if (c.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE contract can be completed");
        }
        c.setStatus(ContractStatus.COMPLETED);
        return toResponse(c);
    }

    @Override
    public ContractResponse terminate(Long contractId, String reason) {
        Contract c = getEntity(contractId);
        if (c.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE contract can be terminated");
        }
        c.setStatus(ContractStatus.TERMINATED);
        // nếu muốn lưu reason thì append vào terms/note (bạn có thể thêm field note riêng)
        c.setTerms(appendNote(c.getTerms(), "[Terminated] " + (reason == null ? "" : reason)));
        return toResponse(c);
    }

    @Override
    public ContractResponse cancel(Long contractId, String reason) {
        Contract c = getEntity(contractId);
        if (c.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT contract can be cancelled");
        }
        c.setStatus(ContractStatus.CANCELLED);
        c.setTerms(appendNote(c.getTerms(), "[Cancelled] " + (reason == null ? "" : reason)));
        return toResponse(c);
    }

    @Override
    public ContractResponse updateTerms(Long contractId, UpdateContractTermsRequest req) {
        Contract c = getEntity(contractId);
        if (c.getStatus() == ContractStatus.CANCELLED || c.getStatus() == ContractStatus.TERMINATED
                || c.getStatus() == ContractStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update terms for a finished/terminated/cancelled contract");
        }
        c.setTerms(req.terms);
        return toResponse(c);
    }

    @Transactional(readOnly = true)
    @Override
    public ContractResponse get(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContractResponse> getAll() {
        return contractRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContractResponse> getByDealer(Long dealerId) {
        return contractRepo.findByDealer_DealerId(dealerId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContractResponse> getByStatus(ContractStatus status) {
        return contractRepo.findByStatus(status).stream().map(this::toResponse).toList();
    }

    // Helpers
    private Contract getEntity(Long id) {
        return contractRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
    }

    private String appendNote(String base, String add) {
        return (base == null || base.isBlank()) ? add : (base + " | " + add);
    }

    private ContractResponse toResponse(Contract c) {
        var res = new ContractResponse();
        res.contractId   = c.getContractId();
        res.dealerId     = c.getDealer() != null ? c.getDealer().getDealerId() : null;
        res.quotationId  = c.getQuotation() != null ? c.getQuotation().getId() : null;
        res.orderId      = c.getOrder() != null ? c.getOrder().getOrderId() : null; // giả định Order có getOrderId()
        res.signedAt     = c.getSignedAt();
        res.contractValue= c.getContractValue();
        res.terms        = c.getTerms();
        res.status       = c.getStatus();

        if (c.getQuotation() != null) {
            res.quotationStatus = c.getQuotation().getStatus().name();
            res.note            = c.getQuotation().getNote();
        }
        if (c.getDealer() != null) {
            res.dealerName = c.getDealer().getDealerName(); // giả định Dealer có dealerName
        }
        return res;
    }
}
