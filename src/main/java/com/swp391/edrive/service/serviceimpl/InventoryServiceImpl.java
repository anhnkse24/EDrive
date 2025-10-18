package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.DealerInventoryAdjustRequest;
import com.swp391.edrive.dto.request.DealerInventoryUpdateRequest;
import com.swp391.edrive.dto.request.DealerInventoryUpsertRequest;
import com.swp391.edrive.dto.response.DealerInventoryResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.DealerInventory;
import com.swp391.edrive.entity.VersionColor;
import com.swp391.edrive.repository.DealerInventoryRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.VersionColorRepository;
import com.swp391.edrive.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {
    private final DealerInventoryRepository inventoryRepo;
    private final DealerRepository dealerRepo;
    private final VersionColorRepository versionColorRepo;

    // ===== READ =====

    @Override
    @Transactional(readOnly = true)
    public List<DealerInventoryResponse> list(Long dealerId, Long versionId, Long versionColorId, boolean onlyAvailable) {
        Dealer dealer = mustGetDealer(dealerId);

        // Bạn có thể viết các query riêng trong repo; ở đây tôi demo load rồi lọc đơn giản.
        List<DealerInventory> rows = inventoryRepo.findAllByDealer_DealerId(dealerId);

        return rows.stream()
                .filter(di -> versionId == null || di.getVersionColor().getVersion().getId().equals(versionId))
                .filter(di -> versionColorId == null || di.getVersionColor().getId().equals(versionColorId))
                .filter(di -> !onlyAvailable || (di.getOnHand() - di.getReserved()) > 0)
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int getDemoCapacityByVersion(Long dealerId, Long versionId) {
        return Math.max(inventoryRepo.sumAvailableByDealerAndVersion(dealerId, versionId), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public int getDemoCapacityByVersionColor(Long dealerId, Long versionColorId) {
        return Math.max(inventoryRepo.sumAvailableByDealerAndVersionColor(dealerId, versionColorId), 0);
    }

    // ===== WRITE =====

    @Override
    public DealerInventoryResponse upsert(Long dealerId, DealerInventoryUpsertRequest req) {
        Dealer dealer = mustGetDealer(dealerId);
        VersionColor vc = mustGetVersionColor(req.getVersionColorId());

        DealerInventory inv = inventoryRepo.findByDealer_DealerIdAndVersionColor_Id(dealerId, vc.getId())
                .orElseGet(() -> {
                    DealerInventory x = new DealerInventory();
                    x.setDealer(dealer);
                    x.setVersionColor(vc);
                    return x;
                });

        requireNonNegative(req.getOnHand(), "onHand");
        requireNonNegative(req.getReserved(), "reserved");

        inv.setOnHand(req.getOnHand());
        inv.setReserved(req.getReserved());

        inv = inventoryRepo.save(inv);
        return toDto(inv);
    }

    @Override
    public DealerInventoryResponse update(Long dealerId, Long inventoryId, DealerInventoryUpdateRequest req) {
        DealerInventory inv = mustGetInventory(inventoryId, dealerId);
        requireNonNegative(req.getOnHand(), "onHand");
        requireNonNegative(req.getReserved(), "reserved");

        inv.setOnHand(req.getOnHand());
        inv.setReserved(req.getReserved());
        return toDto(inv);
    }

    @Override
    public DealerInventoryResponse adjust(Long dealerId, Long inventoryId, DealerInventoryAdjustRequest req) {
        DealerInventory inv = mustGetInventory(inventoryId, dealerId);

        int onHand = inv.getOnHand();
        int reserved = inv.getReserved();

        if (req.getOnHandDelta() != null) onHand += req.getOnHandDelta();
        if (req.getReservedDelta() != null) reserved += req.getReservedDelta();

        if (onHand < 0 || reserved < 0) {
            throw new IllegalArgumentException("onHand/reserved cannot be negative after adjustment");
        }

        inv.setOnHand(onHand);
        inv.setReserved(reserved);
        return toDto(inv);
    }

    @Override
    public void delete(Long dealerId, Long inventoryId) {
        DealerInventory inv = mustGetInventory(inventoryId, dealerId);
        inventoryRepo.delete(inv);
    }

    // ===== helpers =====

    private Dealer mustGetDealer(Long dealerId) {
        return dealerRepo.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found: " + dealerId));
    }

    private VersionColor mustGetVersionColor(Long versionColorId) {
        return versionColorRepo.findById(versionColorId)
                .orElseThrow(() -> new IllegalArgumentException("VersionColor not found: " + versionColorId));
    }

    private DealerInventory mustGetInventory(Long inventoryId, Long dealerId) {
        return inventoryRepo.findById(inventoryId)
                .filter(inv -> inv.getDealer() != null && inv.getDealer().getDealerId().equals(dealerId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inventory not found for dealerId=" + dealerId + ", inventoryId=" + inventoryId));
    }

    private void requireNonNegative(Integer v, String field) {
        if (v == null || v < 0) throw new IllegalArgumentException(field + " must be >= 0");
    }

    private DealerInventoryResponse toDto(DealerInventory di) {
        var vc = di.getVersionColor();
        var v = vc.getVersion();
        var m  = v.getModel();
        return DealerInventoryResponse.builder()
                .inventoryId(di.getId())
                .dealerId(di.getDealer().getDealerId())
                .modelId(m.getId())
                .versionId(v.getId())
                .versionColorId(vc.getId())
                .modelName(v.getModel().getModelName())
                .versionName(v.getVersionName())
                .colorName(vc.getColorName())
                .colorCode(vc.getColorCode())
                .onHand(di.getOnHand())
                .reserved(di.getReserved())
                .available(Math.max(di.getOnHand() - di.getReserved(), 0))
                .build();
    }
}
