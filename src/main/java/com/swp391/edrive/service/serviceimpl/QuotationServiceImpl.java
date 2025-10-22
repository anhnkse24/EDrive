package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CreateQuotationRequest;
import com.swp391.edrive.dto.response.QuotationItemResponse;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.QuotationKind;
import com.swp391.edrive.enums.QuotationStatus;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.repository.VersionColorRepository;
import com.swp391.edrive.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationServiceImpl implements QuotationService {
    private final QuotationRepository quotationRepo;
    private final DealerRepository dealerRepo;
    private final CustomerRepository customerRepo;
    private final VersionColorRepository versionColorRepo;
    // private final PricingService pricingService;
    @Override
    public List<QuotationResponse> getAll() {
        return quotationRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuotationResponse getById(Long id) {
        Quotation q = quotationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
        return toResponse(q);
    }

    @Override
    public List<QuotationResponse> getByDealer(Long dealerId) {
        return quotationRepo.findByDealer_DealerId(dealerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuotationResponse> getByStatus(QuotationStatus status) {
        return quotationRepo.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuotationResponse createDraft(CreateQuotationRequest req) {
        // 1) Dealer bắt buộc
        Dealer dealer = dealerRepo.findById(req.dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));

        // 2) Khởi tạo báo giá
        Quotation q = new Quotation();
        q.setDealer(dealer);

        // 3) Kind: mặc định PURCHASE nếu client không gửi
        QuotationKind kind = (req.kind != null) ? req.kind : QuotationKind.PURCHASE;
        q.setKind(kind);

        // 4) Customer theo kind
        if (kind == QuotationKind.RETAIL) {
            if (req.customerId == null) {
                throw new IllegalArgumentException("customerId is required for RETAIL quotation");
            }
            Customer customer = customerRepo.findById(req.customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            q.setCustomer(customer);
        } else {
            // PURCHASE: chưa gắn khách hàng
            q.setCustomer(null);
        }

        // 5) Meta + trạng thái
        q.setValidUntil(req.validUntil);
        q.setNote(req.note);
        q.setStatus(QuotationStatus.DRAFT);

        // 6) Items bắt buộc
        if (req.items == null || req.items.isEmpty()) {
            throw new IllegalArgumentException("Quotation must contain at least 1 item");
        }

        // 7) Thêm item (snapshot tên + giá)
        for (CreateQuotationRequest.Item it : req.items) {
            VersionColor vc = versionColorRepo.findById(it.versionColorId)
                    .orElseThrow(() -> new IllegalArgumentException("VersionColor not found: " + it.versionColorId));

            QuotationItem qi = new QuotationItem();
            qi.setVersionColor(vc);

            // SNAPSHOT tên theo entity bạn gửi
            if (vc.getVersion() != null) {
                var v = vc.getVersion();                    // VehicleVersion
                qi.setVersionName(v.getVersionName());      // ví dụ: "Premium"
                if (v.getModel() != null) {
                    qi.setModelName(v.getModel().getModelName()); // ví dụ: "VF8"
                }
            }
            qi.setColorName(vc.getColorName());            // VersionColor có colorName trực tiếp

            // Số lượng + đơn giá (fallback nếu client không gửi)
            int qty = (it.quantity == null || it.quantity < 1) ? 1 : it.quantity;
            qi.setQuantity(qty);
            qi.setUnitPrice(it.unitPrice != null ? it.unitPrice : vc.retailPrice());

            // Thêm vào quotation (set back-ref + entity tự tính lineTotal & grandTotal)
            q.addItem(qi);
        }

        Quotation saved = quotationRepo.save(q);
        return toResponse(saved);
    }

    @Override
    public QuotationResponse send(Long id) {
        Quotation q = quotationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
        if (q.getStatus() != QuotationStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT can be SENT");
        }
        q.setStatus(QuotationStatus.SENT);
        return toResponse(q);
    }

    @Override
    public QuotationResponse approve(Long id) {
        Quotation q = quotationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        if (q.getStatus() != QuotationStatus.SENT) {
            throw new IllegalStateException("Only SENT can be APPROVED");
        }
        if (q.getValidUntil() != null && q.getValidUntil().isBefore(java.time.LocalDate.now())) {
            q.setStatus(QuotationStatus.EXPIRED);
            throw new IllegalStateException("Quotation expired");
        }
        q.setStatus(QuotationStatus.APPROVED);
        return toResponse(q);
    }

    @Override
    public QuotationResponse cancel(Long id, String reason) {
        var q = quotationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
        if (q.getStatus() == QuotationStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel an APPROVED quotation");
        }
        q.setStatus(QuotationStatus.CANCELLED);
        q.setNote(appendNote(q.getNote(), "Cancelled: " + (reason == null ? "" : reason)));
        return toResponse(q);
    }

    @Override
    @Transactional(readOnly = true)
    public QuotationResponse get(Long id) {
        Quotation q = quotationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
        return toResponse(q);
    }

    // helper
    private String appendNote(String note, String add) {
        return (note == null || note.isBlank()) ? add : (note + " | " + add);
    }

    private QuotationResponse toResponse(Quotation q) {
        var res = new QuotationResponse();

        res.id = q.getId();
        res.dealerId   = (q.getDealer()   != null ? q.getDealer().getDealerId()     : null);
        res.customerId = (q.getCustomer() != null ? q.getCustomer().getCustomerId() : null);

        res.createdAt  = q.getCreatedAt();
        res.validUntil = q.getValidUntil();
        res.status     = q.getStatus();
        res.grandTotal = q.getGrandTotal();
        res.note       = q.getNote();

        var items = (q.getItems() != null ? q.getItems() : java.util.Collections.<QuotationItem>emptyList());
        res.items = items.stream()
                .map(i -> {
                    var r = new QuotationItemResponse();
                    r.id = i.getId();
                    r.versionColorId = (i.getVersionColor() != null ? i.getVersionColor().getId() : null);
                    r.modelName   = i.getModelName();
                    r.versionName = i.getVersionName();
                    r.colorName   = i.getColorName();
                    r.quantity    = i.getQuantity();
                    r.unitPrice   = i.getUnitPrice();
                    r.lineTotal   = i.getLineTotal();
                    return r;
                })
                .toList();

        return res;
    }
}
