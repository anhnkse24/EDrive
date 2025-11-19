package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.response.AppliedPromotionResponse;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.dto.response.SelectedServiceResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.PromoTarget;
import com.swp391.edrive.enums.QuotationStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdditionalServicesRepository additionalServicesRepository;

    @Autowired
    private PromotionRepository promotionRepository;


    @Override
    public QuotationResponse createQuotation(QuotationRequest quotationRequest, User createdByUser) {
        // Kiểm tra user có dealer không
        if (createdByUser.getDealer() == null) {
            throw new RuntimeException("User không thuộc đại lý nào. Chỉ nhân viên đại lý mới có thể tạo báo giá");
        }

        Dealer dealer = createdByUser.getDealer();

        // Lấy thông tin xe
        Vehicle vehicle = vehicleRepository.findById(quotationRequest.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + quotationRequest.getVehicleId()));

        // Lấy thông tin khách hàng
        Customer customer = customerRepository.findById(quotationRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + quotationRequest.getCustomerId()));

        // Tính toán giá trị báo giá
        BigDecimal unitPrice = vehicle.getPriceRetail(); // Giá xe ban đầu

        // Áp dụng promotion được nhân viên chọn (thay vì tự động tìm)
        PromotionCalculationResult promotionResult = applySelectedPromotions(
            quotationRequest.getSelectedPromotionIds(),
            vehicle,
            customer,
            dealer,
            unitPrice
        );
        BigDecimal promotionDiscountAmount = promotionResult.getTotalDiscount();
        List<AppliedPromotionInfo> appliedPromotions = promotionResult.getAppliedPromotions();

        // Tính giá sau giảm giá
        BigDecimal priceAfterDiscount = unitPrice.subtract(promotionDiscountAmount);

        // Xử lý dịch vụ bổ sung từ database (chỉ hỗ trợ selectedServiceIds)
        BigDecimal additionalServicesTotal = BigDecimal.ZERO;
        List<SelectedServiceResponse> selectedServiceResponses = null;

        if (quotationRequest.getSelectedServiceIds() != null && !quotationRequest.getSelectedServiceIds().isEmpty()) {
            // Lấy các dịch vụ từ database
            List<AdditionalServices> selectedServices = additionalServicesRepository.findAllById(quotationRequest.getSelectedServiceIds());

            // Kiểm tra tất cả service có tồn tại
            if (selectedServices.size() != quotationRequest.getSelectedServiceIds().size()) {
                throw new RuntimeException("Một số dịch vụ không tồn tại trong hệ thống");
            }

            // Lọc chỉ lấy các service đang active
            List<AdditionalServices> activeServices = selectedServices.stream()
                    .filter(AdditionalServices::getIsActive)
                    .collect(Collectors.toList());

            if (activeServices.size() != selectedServices.size()) {
                throw new RuntimeException("Một số dịch vụ không còn hoạt động, vui lòng chọn lại");
            }

            // Tính tổng giá
            additionalServicesTotal = activeServices.stream()
                    .map(AdditionalServices::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Build response từ danh sách services
            selectedServiceResponses = buildSelectedServicesResponse(activeServices);
        }

        // Tính VAT 10% (áp dụng cho cả giá xe và dịch vụ bổ sung)
        BigDecimal totalBeforeVat = priceAfterDiscount.add(additionalServicesTotal);
        BigDecimal vatAmount = totalBeforeVat.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);

        // Tổng giá cuối cùng = (Giá gốc - Giảm giá) + Dịch vụ bổ sung + VAT
        BigDecimal grandTotal = totalBeforeVat.add(vatAmount);

        // Tạo báo giá
        Quotation quotation = new Quotation();
        quotation.setDealer(dealer);  // Lưu thông tin dealer
        quotation.setVehicle(vehicle);
        quotation.setCustomer(customer);
        quotation.setQuotedPrice(unitPrice.doubleValue());
        quotation.setUnitPrice(unitPrice);
        quotation.setPromotionDiscountAmount(promotionDiscountAmount);
        quotation.setPriceAfterPromotion(grandTotal);

        // Lưu các promotion đã áp dụng
        if (!appliedPromotions.isEmpty()) {
            Set<Promotion> promotionSet = appliedPromotions.stream()
                    .map(AppliedPromotionInfo::getPromotion)
                    .collect(Collectors.toSet());
            quotation.setPromotions(promotionSet);
        }

        // Lưu báo giá trước để có ID
        Quotation savedQuotation = quotationRepository.save(quotation);

        // Tạo và lưu các QuotationService entities nếu có selectedServiceIds
        if (quotationRequest.getSelectedServiceIds() != null && !quotationRequest.getSelectedServiceIds().isEmpty()) {
            List<AdditionalServices> selectedServices = additionalServicesRepository.findAllById(quotationRequest.getSelectedServiceIds());

            List<QuotationServices> quotationServices = new java.util.ArrayList<>();
            for (AdditionalServices service : selectedServices) {
                if (service.getIsActive()) {
                    QuotationServices qs = new QuotationServices();
                    qs.setQuotation(savedQuotation);
                    qs.setService(service);
                    qs.setPriceAtSelection(service.getPrice()); // Lưu giá tại thời điểm chọn
                    qs.setQuantity(1);
                    quotationServices.add(qs);
                }
            }
            savedQuotation.setQuotationServices(quotationServices);
            // Save lại để persist quotationServices
            savedQuotation = quotationRepository.save(savedQuotation);
        }

        // Chuyển đổi sang Response
        return QuotationResponse.builder()
                .quotationId(savedQuotation.getQuotationId())
                // Thông tin đại lý và người tạo
                .dealerId(dealer.getDealerId())
                .dealerName(dealer.getDealerName())
                .createdByUserName(createdByUser.getFullName())
                // Thông tin xe
                .vehicleId(vehicle.getVehicleId())
                .modelName(vehicle.getModelName())
                .version(vehicle.getVersion())
                .batteryCapacityKwh(vehicle.getBatteryCapacityKwh())
                .rangeKm(vehicle.getRangeKm())
                .maxSpeedKmh(vehicle.getMaxSpeedKmh())
                .chargingTimeHours(vehicle.getChargingTimeHours())
                .seatingCapacity(vehicle.getSeatingCapacity())
                .motorPowerKw(vehicle.getMotorPowerKw())
                .weightKg(vehicle.getWeightKg())
                .lengthMm(vehicle.getLengthMm())
                .widthMm(vehicle.getWidthMm())
                .heightMm(vehicle.getHeightMm())
                .imageUrl(vehicle.getImageUrl())
                .manufactureYear(vehicle.getManufactureYear())
                // Thông tin khách hàng
                .customerId(customer.getCustomerId())
                .customerFullName(customer.getFullName())
                .customerDob(customer.getDob())
                .customerGender(customer.getGender())
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhone())
                .customerAddress(customer.getAddress())
                .customerIdCardNo(customer.getIdCardNo())
                // Thông tin thanh toán
                .quotationStatus(savedQuotation.getQuotationStatus() != null ? savedQuotation.getQuotationStatus().name() : "PENDING")
                // Dịch vụ bổ sung
                .selectedServices(selectedServiceResponses)
                // Khuyến mãi đã áp dụng
                .appliedPromotions(buildAppliedPromotionsResponse(appliedPromotions))
                // Chi tiết giá
                .unitPrice(unitPrice)
                .promotionDiscountAmount(promotionDiscountAmount)
                .additionalServicesTotal(additionalServicesTotal)
                .vatAmount(vatAmount)
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    public List<QuotationResponse> getAllQuotations() {
        List<Quotation> quotations = quotationRepository.findAll(); // Lấy tất cả báo giá
        return quotations.stream()
                .map(this::convertToQuotationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<QuotationResponse> getQuotationById(Long quotationId) {
        Optional<Quotation> quotation = quotationRepository.findById(quotationId);
        return quotation.map(this::convertToQuotationResponse);
    }


    /**
     * Xây dựng response cho dịch vụ bổ sung
     */


    @Override
    public QuotationResponse updateQuotationStatus(Long quotationId, String status, String rejectionReason) {
        // Tìm quotation
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với ID: " + quotationId));

        // Kiểm tra trạng thái hiện tại phải là PENDING
        if (quotation.getQuotationStatus() != QuotationStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể cập nhật trạng thái cho báo giá đang ở trạng thái PENDING");
        }

        // Validate status
        if (!"ACCEPTED".equals(status) && !"REJECTED".equals(status)) {
            throw new IllegalArgumentException("Trạng thái chỉ có thể là ACCEPTED hoặc REJECTED");
        }

        // Cập nhật status
        if ("ACCEPTED".equals(status)) {
            quotation.setQuotationStatus(QuotationStatus.ACCEPTED);
        } else {
            quotation.setQuotationStatus(QuotationStatus.REJECTED);
            // Có thể lưu lý do từ chối vào note field nếu cần (hiện tại entity chưa có)
        }

        // Lưu và trả về response
        Quotation savedQuotation = quotationRepository.save(quotation);
        return convertToQuotationResponse(savedQuotation);
    }

    /**
     * Áp dụng các promotion được nhân viên chọn (thay vì tự động tìm)
     */
    private PromotionCalculationResult applySelectedPromotions(
            List<Long> selectedPromotionIds,
            Vehicle vehicle,
            Customer customer,
            Dealer dealer,
            BigDecimal unitPrice) {

        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<AppliedPromotionInfo> appliedPromotions = new java.util.ArrayList<>();

        // Nếu không chọn promotion nào, trả về kết quả rỗng
        if (selectedPromotionIds == null || selectedPromotionIds.isEmpty()) {
            return new PromotionCalculationResult(BigDecimal.ZERO, appliedPromotions);
        }

        // Lấy các promotion được chọn
        List<Promotion> selectedPromotions = promotionRepository.findAllById(selectedPromotionIds);

        // Kiểm tra tất cả promotion có tồn tại
        if (selectedPromotions.size() != selectedPromotionIds.size()) {
            throw new RuntimeException("Một số khuyến mãi không tồn tại trong hệ thống");
        }

        LocalDate today = LocalDate.now();

        for (Promotion promo : selectedPromotions) {
            // Validation 1: Kiểm tra promotion thuộc về dealer
            if (!promo.getDealer().getDealerId().equals(dealer.getDealerId())) {
                throw new RuntimeException("Khuyến mãi '" + promo.getTitle() + "' không thuộc về đại lý này");
            }

            // Validation 2: Kiểm tra promotion còn hiệu lực
            if (promo.getStartDate() != null && today.isBefore(promo.getStartDate())) {
                throw new RuntimeException("Khuyến mãi '" + promo.getTitle() + "' chưa bắt đầu");
            }
            if (promo.getEndDate() != null && today.isAfter(promo.getEndDate())) {
                throw new RuntimeException("Khuyến mãi '" + promo.getTitle() + "' đã hết hạn");
            }

            // Validation 3: Kiểm tra promotion có áp dụng được cho vehicle/customer không
            boolean isApplicable = false;
            String notApplicableReason = "";

            if (promo.getApplicableTo() == PromoTarget.VEHICLE) {
                if (promo.getVehicles() != null && promo.getVehicles().contains(vehicle)) {
                    isApplicable = true;
                } else {
                    notApplicableReason = "không áp dụng cho xe " + vehicle.getModelName();
                }
            } else if (promo.getApplicableTo() == PromoTarget.CUSTOMER) {
                if (promo.getCustomers() != null && promo.getCustomers().contains(customer)) {
                    isApplicable = true;
                } else {
                    notApplicableReason = "không áp dụng cho khách hàng " + customer.getFullName();
                }
            }

            if (!isApplicable) {
                throw new RuntimeException("Khuyến mãi '" + promo.getTitle() + "' " + notApplicableReason);
            }

            // Tính giảm giá
            BigDecimal discount = BigDecimal.ZERO;

            if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
                // Giảm theo phần trăm
                discount = unitPrice.multiply(BigDecimal.valueOf(promo.getDiscountValue()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else if (promo.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                // Giảm số tiền cố định
                discount = BigDecimal.valueOf(promo.getDiscountValue());
            }

            totalDiscount = totalDiscount.add(discount);

            // Lưu thông tin promotion đã áp dụng
            appliedPromotions.add(new AppliedPromotionInfo(promo, discount));
        }

        // Đảm bảo tổng giảm giá không vượt quá giá xe
        if (totalDiscount.compareTo(unitPrice) > 0) {
            totalDiscount = unitPrice;
        }

        return new PromotionCalculationResult(totalDiscount, appliedPromotions);
    }

    /**
     * Build response list cho applied promotions
     */
    private List<AppliedPromotionResponse> buildAppliedPromotionsResponse(List<AppliedPromotionInfo> appliedPromotions) {
        if (appliedPromotions == null || appliedPromotions.isEmpty()) {
            return null;
        }

        return appliedPromotions.stream()
                .map(info -> AppliedPromotionResponse.builder()
                        .title(info.getPromotion().getTitle())
                        .description(info.getPromotion().getDescription())
                        .discountType(info.getPromotion().getDiscountType().name())
                        .discountValue(info.getPromotion().getDiscountValue())
                        .discountAmount(info.getDiscountAmount())
                        .applicableTo(info.getPromotion().getApplicableTo().name())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Helper class để lưu thông tin promotion đã áp dụng
     */
    private static class AppliedPromotionInfo {
        private final Promotion promotion;
        private final BigDecimal discountAmount;

        public AppliedPromotionInfo(Promotion promotion, BigDecimal discountAmount) {
            this.promotion = promotion;
            this.discountAmount = discountAmount;
        }

        public Promotion getPromotion() {
            return promotion;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }
    }

    /**
     * Helper class để trả về kết quả tính promotion
     */
    private static class PromotionCalculationResult {
        private final BigDecimal totalDiscount;
        private final List<AppliedPromotionInfo> appliedPromotions;

        public PromotionCalculationResult(BigDecimal totalDiscount, List<AppliedPromotionInfo> appliedPromotions) {
            this.totalDiscount = totalDiscount;
            this.appliedPromotions = appliedPromotions;
        }

        public BigDecimal getTotalDiscount() {
            return totalDiscount;
        }

        public List<AppliedPromotionInfo> getAppliedPromotions() {
            return appliedPromotions;
        }
    }

    /**
     * MỚI: Xây dựng response từ danh sách services động lấy từ database
     * Trả về danh sách dịch vụ thực tế thay vì Boolean flags
     */
    private List<SelectedServiceResponse> buildSelectedServicesResponse(List<AdditionalServices> services) {
        return services.stream()
                .map(service -> SelectedServiceResponse.builder()
                        .serviceId(service.getServiceId())
                        .serviceName(service.getServiceName())
                        .price(service.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Quotation entity sang QuotationResponse
     */
    private QuotationResponse convertToQuotationResponse(Quotation quotation) {
        Vehicle vehicle = quotation.getVehicle();
        Customer customer = quotation.getCustomer();
        Dealer dealer = quotation.getDealer();

        // Tính toán lại các giá trị
        BigDecimal unitPrice = quotation.getUnitPrice();
        BigDecimal promotionDiscountAmount = quotation.getPromotionDiscountAmount() != null
                ? quotation.getPromotionDiscountAmount()
                : BigDecimal.ZERO;

        // Tính tổng dịch vụ bổ sung từ quotationServices
        BigDecimal additionalServicesTotal = BigDecimal.ZERO;
        List<SelectedServiceResponse> selectedServiceResponses = null;

        if (quotation.getQuotationServices() != null && !quotation.getQuotationServices().isEmpty()) {
            additionalServicesTotal = quotation.getQuotationServices().stream()
                    .map(qs -> qs.getPriceAtSelection().multiply(BigDecimal.valueOf(qs.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Build list of selected services
            selectedServiceResponses = quotation.getQuotationServices().stream()
                    .map(qs -> SelectedServiceResponse.builder()
                            .serviceId(qs.getService().getServiceId())
                            .serviceName(qs.getService().getServiceName())
                            .price(qs.getPriceAtSelection())
                            .build())
                    .collect(Collectors.toList());
        }

        BigDecimal priceAfterDiscount = unitPrice.subtract(promotionDiscountAmount);
        BigDecimal totalBeforeVat = priceAfterDiscount.add(additionalServicesTotal);
        BigDecimal vatAmount = totalBeforeVat.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = totalBeforeVat.add(vatAmount);

        // Build applied promotions từ quotation.promotions
        List<AppliedPromotionResponse> appliedPromotionsResponse = null;
        if (quotation.getPromotions() != null && !quotation.getPromotions().isEmpty()) {
            appliedPromotionsResponse = quotation.getPromotions().stream()
                    .map(promo -> AppliedPromotionResponse.builder()
                            .title(promo.getTitle())
                            .description(promo.getDescription())
                            .discountType(promo.getDiscountType().name())
                            .discountValue(promo.getDiscountValue())
                            .discountAmount(null) // Không tính lại, chỉ hiển thị thông tin
                            .applicableTo(promo.getApplicableTo().name())
                            .build())
                    .collect(Collectors.toList());
        }

        return QuotationResponse.builder()
                .quotationId(quotation.getQuotationId())
                // Thông tin đại lý
                .dealerId(dealer != null ? dealer.getDealerId() : null)
                .dealerName(dealer != null ? dealer.getDealerName() : null)
                .createdByUserName(dealer != null ? dealer.getDealerName() : "N/A")
                // Thông tin xe
                .vehicleId(vehicle.getVehicleId())
                .modelName(vehicle.getModelName())
                .version(vehicle.getVersion())
                .batteryCapacityKwh(vehicle.getBatteryCapacityKwh())
                .rangeKm(vehicle.getRangeKm())
                .maxSpeedKmh(vehicle.getMaxSpeedKmh())
                .chargingTimeHours(vehicle.getChargingTimeHours())
                .seatingCapacity(vehicle.getSeatingCapacity())
                .motorPowerKw(vehicle.getMotorPowerKw())
                .weightKg(vehicle.getWeightKg())
                .lengthMm(vehicle.getLengthMm())
                .widthMm(vehicle.getWidthMm())
                .heightMm(vehicle.getHeightMm())
                .imageUrl(vehicle.getImageUrl())
                .manufactureYear(vehicle.getManufactureYear())
                // Thông tin khách hàng
                .customerId(customer.getCustomerId())
                .customerFullName(customer.getFullName())
                .customerDob(customer.getDob())
                .customerGender(customer.getGender())
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhone())
                .customerAddress(customer.getAddress())
                .customerIdCardNo(customer.getIdCardNo())
                // Thông tin thanh toán
                .paymentMethod(quotation.getPaymentMethod() != null ? quotation.getPaymentMethod().name() : null)
                .quotationStatus(quotation.getQuotationStatus() != null ? quotation.getQuotationStatus().name() : "PENDING")
                // Dịch vụ bổ sung
                .selectedServices(selectedServiceResponses)
                // Khuyến mãi đã áp dụng
                .appliedPromotions(appliedPromotionsResponse)
                // Chi tiết giá
                .unitPrice(unitPrice)
                .promotionDiscountAmount(promotionDiscountAmount)
                .additionalServicesTotal(additionalServicesTotal)
                .vatAmount(vatAmount)
                .grandTotal(grandTotal)
                .build();
    }
}
