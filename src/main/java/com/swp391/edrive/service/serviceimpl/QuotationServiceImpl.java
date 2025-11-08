package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.QuotationCreateRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.entity.Quotation;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotationServiceImpl implements QuotationService {

    private final VehicleRepository vehicleRepo;
    private final QuotationRepository quotationRepo;

    @Value("${edrive.vat-rate:0.10}")          // nếu muốn inject từ config
    private BigDecimal vatRate;

    private static final BigDecimal DISCOUNT_RATE_FOR_ONE = new BigDecimal("0.05");

    @Override
    public QuotationResponse previewQuotation(QuotationCreateRequest r) {
        var v = vehicleRepo.findById(r.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + r.getVehicleId()));

        BigDecimal unitPrice = v.getPriceRetail();
        BigDecimal discountRate = discountRateByQty(1);
        BigDecimal discountAmount = unitPrice.multiply(discountRate);
        BigDecimal subtotalAfterDiscount = unitPrice.subtract(discountAmount);

        BigDecimal serviceTotal = BigDecimal.ZERO;
        if (r.isIncludeInsurancePercent())
            serviceTotal = serviceTotal.add(unitPrice.multiply(new BigDecimal("0.03")));
        if (r.isIncludeWarrantyExtension())
            serviceTotal = serviceTotal.add(new BigDecimal("50000000"));
        if (r.isIncludeAccessories())
            serviceTotal = serviceTotal.add(new BigDecimal("30000000"));


        BigDecimal taxableBase = subtotalAfterDiscount.add(serviceTotal);
        BigDecimal vatAmount = taxableBase.multiply(vatRate).setScale(0, RoundingMode.HALF_UP);
        BigDecimal grandTotal = taxableBase.add(vatAmount);

        String fullAddress = String.join(", ",
                r.getStreet(), r.getWard(), r.getDistrict(), r.getCity()
        );

        return QuotationResponse.builder()
                .vehicleId(v.getVehicleId())
                .vehicleModel(v.getModelName() + " - " + v.getVersion())
                .vehicleImageUrl(v.getImageUrl())
                .unitPrice(unitPrice)
                .includeInsurancePercent(r.isIncludeInsurancePercent())
                .includeWarrantyExtension(r.isIncludeWarrantyExtension())
                .includeAccessories(r.isIncludeAccessories())
                .discountRate(discountRate)
                .discountAmount(discountAmount)
                .vehicleSubtotal(unitPrice)
                .serviceTotal(serviceTotal)
                .subtotalAfterDiscount(subtotalAfterDiscount)
                .taxableBase(taxableBase)
                .vatRate(vatRate)
                .vatAmount(vatAmount)
                .grandTotal(grandTotal)
                .customerFullName(r.getCustomerFullName())
                .phone(r.getPhone())
                .email(r.getEmail())
                .fullAddress(fullAddress)
                .notes(r.getNotes())
                .build();
    }


    @Override
    @Transactional
    public QuotationResponse createQuotation(QuotationCreateRequest r) {
        QuotationResponse calc = previewQuotation(r); // tính giống preview

        Quotation q = new Quotation();
        Vehicle v = new Vehicle();
        v.setVehicleId(calc.getVehicleId());
        q.setVehicle(v);

        q.setUnitPrice(calc.getUnitPrice());
        q.setDiscountRate(calc.getDiscountRate());
        q.setDiscountAmount(calc.getDiscountAmount());

        q.setIncludeInsurancePercent(calc.isIncludeInsurancePercent());
        q.setIncludeWarrantyExtension(calc.isIncludeWarrantyExtension());
        q.setIncludeAccessories(calc.isIncludeAccessories());
        q.setServiceTotal(calc.getServiceTotal());

        q.setVatRate(calc.getVatRate());
        q.setVehicleSubtotal(calc.getVehicleSubtotal());
        q.setSubtotalAfterDiscount(calc.getSubtotalAfterDiscount());
        q.setTaxableBase(calc.getTaxableBase());
        q.setVatAmount(calc.getVatAmount());
        q.setGrandTotal(calc.getGrandTotal());

        q.setCustomerFullName(calc.getCustomerFullName());
        q.setPhone(calc.getPhone());
        q.setEmail(calc.getEmail());
        q.setFullAddress(calc.getFullAddress());
        q.setNotes(calc.getNotes());

        q = quotationRepo.save(q);

        return QuotationResponse.builder()
                .quotationId(q.getQuotationId())
                .vehicleId(q.getVehicle().getVehicleId())
                .vehicleModel(calc.getVehicleModel())
                .vehicleImageUrl(calc.getVehicleImageUrl())
                .unitPrice(q.getUnitPrice())
                .includeInsurancePercent(q.isIncludeInsurancePercent())
                .includeWarrantyExtension(q.isIncludeWarrantyExtension())
                .includeAccessories(q.isIncludeAccessories())
                .discountRate(q.getDiscountRate())
                .discountAmount(q.getDiscountAmount())
                .vehicleSubtotal(q.getVehicleSubtotal())
                .serviceTotal(q.getServiceTotal())
                .subtotalAfterDiscount(q.getSubtotalAfterDiscount())
                .taxableBase(q.getTaxableBase())
                .vatRate(q.getVatRate())
                .vatAmount(q.getVatAmount())
                .grandTotal(q.getGrandTotal())
                .customerFullName(q.getCustomerFullName())
                .phone(q.getPhone())
                .email(q.getEmail())
                .fullAddress(q.getFullAddress())
                .notes(q.getNotes())
                .build();
    }

    @Override
    public QuotationResponse getQuotation(Long id) {
        Quotation q = quotationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found: " + id));

        Vehicle v = q.getVehicle();

        return QuotationResponse.builder()
                .quotationId(q.getQuotationId())
                .vehicleId(v.getVehicleId())
                .vehicleModel(v.getModelName() + " - " + v.getVersion())
                .vehicleImageUrl(v.getImageUrl())
                .unitPrice(q.getUnitPrice())
                .includeInsurancePercent(q.isIncludeInsurancePercent())
                .includeWarrantyExtension(q.isIncludeWarrantyExtension())
                .includeAccessories(q.isIncludeAccessories())
                .discountRate(q.getDiscountRate())
                .discountAmount(q.getDiscountAmount())
                .vehicleSubtotal(q.getVehicleSubtotal())
                .serviceTotal(q.getServiceTotal())
                .subtotalAfterDiscount(q.getSubtotalAfterDiscount())
                .taxableBase(q.getTaxableBase())
                .vatRate(q.getVatRate())
                .vatAmount(q.getVatAmount())
                .grandTotal(q.getGrandTotal())
                .customerFullName(q.getCustomerFullName())
                .phone(q.getPhone())
                .email(q.getEmail())
                .fullAddress(q.getFullAddress())
                .notes(q.getNotes())
                .build();
    }

    private BigDecimal discountRateByQty(int quantity) {
        if (quantity > 10) return new BigDecimal("0.15");
        if (quantity >= 6) return new BigDecimal("0.10");
        if (quantity >= 1) return new BigDecimal("0.05");
        return BigDecimal.ZERO;
    }

    @Override
    public List<QuotationResponse> getAllQuotations() {
        List<Quotation> quotations = quotationRepo.findAll();
        return quotations.stream()
                .map(this::toResponse)
                .toList();
    }

    private QuotationResponse toResponse(Quotation q) {
        Vehicle v = q.getVehicle();

        return QuotationResponse.builder()
                .quotationId(q.getQuotationId())
                .vehicleId(v.getVehicleId())
                .vehicleModel(v.getModelName() + " - " + v.getVersion())
                .vehicleImageUrl(v.getImageUrl())
                .unitPrice(q.getUnitPrice())
                .includeInsurancePercent(q.isIncludeInsurancePercent())
                .includeWarrantyExtension(q.isIncludeWarrantyExtension())
                .includeAccessories(q.isIncludeAccessories())
                .discountRate(q.getDiscountRate())
                .discountAmount(q.getDiscountAmount())
                .vehicleSubtotal(q.getVehicleSubtotal())
                .serviceTotal(q.getServiceTotal())
                .subtotalAfterDiscount(q.getSubtotalAfterDiscount())
                .taxableBase(q.getTaxableBase())
                .vatRate(q.getVatRate())
                .vatAmount(q.getVatAmount())
                .grandTotal(q.getGrandTotal())
                .customerFullName(q.getCustomerFullName())
                .phone(q.getPhone())
                .email(q.getEmail())
                .fullAddress(q.getFullAddress())
                .notes(q.getNotes())
                .build();
    }

}
