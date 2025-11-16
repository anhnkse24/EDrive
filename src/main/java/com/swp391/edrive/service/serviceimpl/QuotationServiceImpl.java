package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.AdditionalServicesRequest;
import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.response.AdditionalServicesResponse;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Giá cố định cho các dịch vụ bổ sung
    private static final BigDecimal TINT_FILM_PRICE = new BigDecimal("8500000");
    private static final BigDecimal WALLBOX_CHARGER_PRICE = new BigDecimal("15000000");
    private static final BigDecimal WARRANTY_EXTENSION_PRICE = new BigDecimal("25000000");
    private static final BigDecimal PPF_PRICE = new BigDecimal("45000000");
    private static final BigDecimal CERAMIC_COATING_PRICE = new BigDecimal("12000000");
    private static final BigDecimal CAMERA_360_PRICE = new BigDecimal("18000000");

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

        // Giá giảm từ khuyến mãi (hiện tại = 0, có thể tích hợp sau)
        BigDecimal promotionDiscountAmount = BigDecimal.ZERO;

        // Tính giá sau giảm giá
        BigDecimal priceAfterDiscount = unitPrice.subtract(promotionDiscountAmount);

        // Xử lý dịch vụ bổ sung
        AdditionalServices additionalServices = null;
        BigDecimal additionalServicesTotal = BigDecimal.ZERO;
        AdditionalServicesResponse additionalServicesResponse = null;

        if (quotationRequest.getAdditionalServices() != null) {
            additionalServices = buildAdditionalServices(quotationRequest.getAdditionalServices());
            additionalServicesTotal = calculateAdditionalServicesTotal(additionalServices);
            additionalServicesResponse = buildAdditionalServicesResponse(additionalServices, additionalServicesTotal);
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
        quotation.setCreatedBy(createdByUser);  // Lưu user tạo báo giá
        quotation.setVehicle(vehicle);
        quotation.setCustomer(customer);
        quotation.setQuotedPrice(unitPrice.doubleValue());
        quotation.setUnitPrice(unitPrice);
        quotation.setPromotionDiscountAmount(promotionDiscountAmount);
        quotation.setPriceAfterPromotion(grandTotal);
        quotation.setPaymentMethod(quotationRequest.getPaymentMethod());
        quotation.setAdditionalServices(additionalServices); // Lưu dịch vụ bổ sung

        // Lưu báo giá
        Quotation savedQuotation = quotationRepository.save(quotation);

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
                .vehicleStatus(vehicle.getStatus() != null ? vehicle.getStatus().toString() : null)
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
                .paymentMethod(quotationRequest.getPaymentMethod() != null ? quotationRequest.getPaymentMethod().name() : null)
                .quotationStatus(savedQuotation.getQuotationStatus() != null ? savedQuotation.getQuotationStatus().name() : "PENDING")
                // Dịch vụ bổ sung
                .additionalServices(additionalServicesResponse)
                // Chi tiết giá
                .unitPrice(unitPrice)
                .promotionDiscountAmount(promotionDiscountAmount)
                .additionalServicesTotal(additionalServicesTotal)
                .vatAmount(vatAmount)
                .grandTotal(grandTotal)
                .build();
    }

    /**
     * Xây dựng đối tượng AdditionalServices từ request
     */
    private AdditionalServices buildAdditionalServices(AdditionalServicesRequest request) {
        AdditionalServices services = new AdditionalServices();

        // Phim cách nhiệt
        services.setHasTintFilm(request.getHasTintFilm() != null && request.getHasTintFilm());
        services.setTintFilmPrice(services.getHasTintFilm() ? TINT_FILM_PRICE : BigDecimal.ZERO);

        // Bộ sạc Wallbox
        services.setHasWallboxCharger(request.getHasWallboxCharger() != null && request.getHasWallboxCharger());
        services.setWallboxChargerPrice(services.getHasWallboxCharger() ? WALLBOX_CHARGER_PRICE : BigDecimal.ZERO);

        // Bảo hành mở rộng
        services.setHasWarrantyExtension(request.getHasWarrantyExtension() != null && request.getHasWarrantyExtension());
        services.setWarrantyExtensionPrice(services.getHasWarrantyExtension() ? WARRANTY_EXTENSION_PRICE : BigDecimal.ZERO);

        // PPF
        services.setHasPPF(request.getHasPPF() != null && request.getHasPPF());
        services.setPpfPrice(services.getHasPPF() ? PPF_PRICE : BigDecimal.ZERO);

        // Ceramic Coating
        services.setHasCeramicCoating(request.getHasCeramicCoating() != null && request.getHasCeramicCoating());
        services.setCeramicCoatingPrice(services.getHasCeramicCoating() ? CERAMIC_COATING_PRICE : BigDecimal.ZERO);

        // Camera 360
        services.setHas360Camera(request.getHas360Camera() != null && request.getHas360Camera());
        services.setCamera360Price(services.getHas360Camera() ? CAMERA_360_PRICE : BigDecimal.ZERO);

        return services;
    }

    /**
     * Tính tổng giá dịch vụ bổ sung
     */
    private BigDecimal calculateAdditionalServicesTotal(AdditionalServices services) {
        BigDecimal total = BigDecimal.ZERO;

        if (services.getHasTintFilm()) {
            total = total.add(TINT_FILM_PRICE);
        }
        if (services.getHasWallboxCharger()) {
            total = total.add(WALLBOX_CHARGER_PRICE);
        }
        if (services.getHasWarrantyExtension()) {
            total = total.add(WARRANTY_EXTENSION_PRICE);
        }
        if (services.getHasPPF()) {
            total = total.add(PPF_PRICE);
        }
        if (services.getHasCeramicCoating()) {
            total = total.add(CERAMIC_COATING_PRICE);
        }
        if (services.getHas360Camera()) {
            total = total.add(CAMERA_360_PRICE);
        }

        return total;
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
    private AdditionalServicesResponse buildAdditionalServicesResponse(AdditionalServices services, BigDecimal total) {
        return AdditionalServicesResponse.builder()
                .hasTintFilm(services.getHasTintFilm())
                .tintFilmPrice(services.getTintFilmPrice())
                .hasWallboxCharger(services.getHasWallboxCharger())
                .wallboxChargerPrice(services.getWallboxChargerPrice())
                .hasWarrantyExtension(services.getHasWarrantyExtension())
                .warrantyExtensionPrice(services.getWarrantyExtensionPrice())
                .hasPPF(services.getHasPPF())
                .ppfPrice(services.getPpfPrice())
                .hasCeramicCoating(services.getHasCeramicCoating())
                .ceramicCoatingPrice(services.getCeramicCoatingPrice())
                .has360Camera(services.getHas360Camera())
                .camera360Price(services.getCamera360Price())
                .totalServicesPrice(total)
                .build();
    }

    private QuotationResponse convertToQuotationResponse(Quotation quotation) {
        Vehicle vehicle = quotation.getVehicle();
        Customer customer = quotation.getCustomer();
        Dealer dealer = quotation.getDealer();
        User createdBy = quotation.getCreatedBy();

        // Lấy thông tin về dịch vụ bổ sung
        AdditionalServicesResponse additionalServicesResponse = null;
        BigDecimal additionalServicesTotal = BigDecimal.ZERO;

        if (quotation.getAdditionalServices() != null) {
            additionalServicesTotal = calculateAdditionalServicesTotal(quotation.getAdditionalServices());
            additionalServicesResponse = buildAdditionalServicesResponse(
                    quotation.getAdditionalServices(),
                    additionalServicesTotal
            );
        }

        return QuotationResponse.builder()
                .quotationId(quotation.getQuotationId())
                // Thông tin đại lý (có thể null nếu báo giá cũ)
                .dealerId(dealer != null ? dealer.getDealerId() : null)
                .dealerName(dealer != null ? dealer.getDealerName() : null)
                .createdByUserName(createdBy != null ? createdBy.getFullName() : null)  // Lấy tên user tạo báo giá
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
                .vehicleStatus(vehicle.getStatus() != null ? vehicle.getStatus().toString() : null)
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
                .quotationStatus(quotation.getQuotationStatus() != null ? quotation.getQuotationStatus().name() : "PENDING")
                .paymentMethod(quotation.getPaymentMethod() != null ? quotation.getPaymentMethod().name() : null)
                .additionalServices(additionalServicesResponse)
                // Chi tiết giá
                .unitPrice(quotation.getUnitPrice())
                .promotionDiscountAmount(quotation.getPromotionDiscountAmount())
                .additionalServicesTotal(additionalServicesTotal)
                .grandTotal(quotation.getPriceAfterPromotion())
                .build();
    }

    @Override
    public QuotationResponse updateQuotationStatus(Long quotationId, String status, String rejectionReason) {
        // Tìm quotation
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với ID: " + quotationId));

        // Kiểm tra trạng thái hiện tại phải là PENDING
        if (quotation.getQuotationStatus() != com.swp391.edrive.enums.QuotationStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể cập nhật trạng thái cho báo giá đang ở trạng thái PENDING");
        }

        // Validate status
        if (!"ACCEPTED".equals(status) && !"REJECTED".equals(status)) {
            throw new IllegalArgumentException("Trạng thái chỉ có thể là ACCEPTED hoặc REJECTED");
        }

        // Cập nhật status
        if ("ACCEPTED".equals(status)) {
            quotation.setQuotationStatus(com.swp391.edrive.enums.QuotationStatus.ACCEPTED);
        } else {
            quotation.setQuotationStatus(com.swp391.edrive.enums.QuotationStatus.REJECTED);
            // Có thể lưu lý do từ chối vào note field nếu cần (hiện tại entity chưa có)
        }

        // Lưu và trả về response
        Quotation savedQuotation = quotationRepository.save(quotation);
        return convertToQuotationResponse(savedQuotation);
    }
}
