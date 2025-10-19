package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.DealerRequest;
import com.swp391.edrive.dto.response.DealerResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.service.DealerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;

    @Override
    @Transactional
    public DealerResponse createDealer(DealerRequest req) {
        Dealer d = new Dealer();
        d.setDealerName(req.getDealerName());
        d.setAddress(req.getAddress());
        d.setContactPerson(req.getContactPerson());
        d.setPhone(req.getPhone());
        d.setContractId(req.getContractId());
        return toResponse(dealerRepository.save(d));
    }

    @Override
    @Transactional
    public DealerResponse updateDealer(Long dealerId, DealerRequest req) {
        Dealer d = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với id = " + dealerId));

        d.setDealerName(req.getDealerName());
        d.setAddress(req.getAddress());
        d.setContactPerson(req.getContactPerson());
        d.setPhone(req.getPhone());
        d.setContractId(req.getContractId());

        return toResponse(dealerRepository.save(d));
    }

    @Override
    @Transactional
    public void deleteDealer(Long dealerId) {
        Dealer d = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với id = " + dealerId));
        dealerRepository.delete(d);
    }

    @Override
    @Transactional(readOnly = true)
    public DealerResponse getDealerById(Long dealerId) {
        Dealer d = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với id = " + dealerId));
        return toResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealerResponse> getAllDealers() {
        return dealerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DealerResponse toResponse(Dealer d) {
        return DealerResponse.builder()
                .dealerId(d.getDealerId())
                .dealerName(d.getDealerName())
                .address(d.getAddress())
                .contactPerson(d.getContactPerson())
                .phone(d.getPhone())
                .contractId(d.getContractId())
                .build();
    }
}