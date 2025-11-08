package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.DealerRequest;
import com.swp391.edrive.dto.response.DealerResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.repository.*;
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
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ContractRepository contractRepository;
    private final DealerInventoryRepository dealerInventoryRepository;
    private final TestDriveRepository testDriveRepository;
    private final QuotationRepository quotationRepository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final PromotionRepository promotionRepository;
    private final FeedbackRepository feedbackRepository;
    private final CustomerRepository customerRepository;

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
        dealer.setEmail(req.getEmail());
        dealer.setPhone(req.getPhone());

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

        // Kiểm tra TẤT CẢ các ràng buộc bằng cách query trực tiếp từ repository

        // 1. Kiểm tra Users
        long userCount = userRepository.findAll().stream()
                .filter(u -> u.getDealer() != null && u.getDealer().getDealerId().equals(dealerId))
                .count();
        if (userCount > 0) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + userCount + " người dùng liên kết.");
        }

        // 2. Kiểm tra Orders
        List<?> orders = orderRepository.findByDealer_DealerId(dealerId);
        if (orders != null && !orders.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + orders.size() + " đơn hàng liên kết.");
        }

        // 3. Kiểm tra Contracts
        List<?> contracts = contractRepository.findByDealer_DealerId(dealerId);
        if (contracts != null && !contracts.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + contracts.size() + " hợp đồng liên kết.");
        }

        // 4. Kiểm tra DealerInventories
        List<?> inventories = dealerInventoryRepository.findAll().stream()
                .filter(inv -> inv.getDealer() != null && inv.getDealer().getDealerId().equals(dealerId))
                .toList();
        if (!inventories.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + inventories.size() + " tồn kho liên kết.");
        }

        // 5. Kiểm tra TestDrives
        List<?> testDrives = testDriveRepository.findByDealer_DealerId(dealerId);
        if (testDrives != null && !testDrives.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + testDrives.size() + " lịch sử lái thử liên kết.");
        }

        // 6. Kiểm tra Quotations
        List<?> quotations = quotationRepository.findAll().stream()
                .filter(q -> q.getDealer() != null && q.getDealer().getDealerId().equals(dealerId))
                .toList();
        if (!quotations.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + quotations.size() + " báo giá liên kết.");
        }

        // 8. Kiểm tra Profiles
        List<?> profiles = profileRepository.findByDealer_DealerId(dealerId);
        if (profiles != null && !profiles.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + profiles.size() + " hồ sơ liên kết.");
        }

        // 9. Kiểm tra Promotions
        List<?> promotions = promotionRepository.findByDealer_DealerId(dealerId);
        if (promotions != null && !promotions.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + promotions.size() + " chương trình khuyến mãi liên kết.");
        }

        // 10. Kiểm tra Feedbacks
        long feedbackCount = feedbackRepository.findAll().stream()
                .filter(f -> f.getDealer() != null && f.getDealer().getDealerId().equals(dealerId))
                .count();
        if (feedbackCount > 0) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + feedbackCount + " phản hồi liên kết.");
        }

        // 11. Kiểm tra Customers
        List<?> customers = customerRepository.findByDealer_DealerId(dealerId);
        if (customers != null && !customers.isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì còn " + customers.size() + " khách hàng liên kết.");
        }

        notificationRepository.deleteAllByDealer_DealerId(dealerId);
        // Nếu tất cả kiểm tra đều pass, xóa dealer
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
                .email(dealer.getEmail())
                .phone(dealer.getPhone())
                .build();
    }
}
