package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.DealerRequest;
import com.swp391.edrive.dto.response.DealerResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.service.DealerService;
import com.swp391.edrive.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public DealerResponse createDealer(DealerRequest req) {
        Dealer dealer = new Dealer();
        dealer.setDealerName(req.getDealerName());
        dealer.setHouseNumberAndStreet(req.getHouseNumberAndStreet());
        dealer.setWardOrCommune(req.getWardOrCommune());
        dealer.setDistrict(req.getDistrict());
        dealer.setProvinceOrCity(req.getProvinceOrCity());
        dealer.setContactPerson(req.getContactPerson());

        Dealer saved = dealerRepository.save(dealer);
        notificationService.createAdminNotificationForDealerRequest(dealer.getDealerId());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public DealerResponse updateDealer(Long dealerId, DealerRequest req) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID = " + dealerId));

        dealer.setDealerName(req.getDealerName());
        dealer.setHouseNumberAndStreet(req.getHouseNumberAndStreet());
        dealer.setWardOrCommune(req.getWardOrCommune());
        dealer.setDistrict(req.getDistrict());
        dealer.setProvinceOrCity(req.getProvinceOrCity());
        dealer.setContactPerson(req.getContactPerson());

        Dealer updated = dealerRepository.save(dealer);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDealer(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID = " + dealerId));
        dealerRepository.delete(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public DealerResponse getDealerById(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID = " + dealerId));
        return toResponse(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealerResponse> getAllDealers() {
        return dealerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DealerResponse toResponse(Dealer dealer) {
        return DealerResponse.builder()
                .dealerId(dealer.getDealerId())
                .dealerName(dealer.getDealerName())
                .houseNumberAndStreet(dealer.getHouseNumberAndStreet())
                .wardOrCommune(dealer.getWardOrCommune())
                .district(dealer.getDistrict())
                .provinceOrCity(dealer.getProvinceOrCity())
                .contactPerson(dealer.getContactPerson())
                .build();
    }
}
