package com.swp391.edrive.mapper.contract;

import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.dto.response.CustomerContractResponse;
import com.swp391.edrive.dto.response.ManufacturerContractResponse;
import com.swp391.edrive.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ContractMapper implements IContractMapper {

    @Override
    public ManufacturerContractResponse toManufacturerContractResponse(Contract c) {
        if (c == null) return null;

        String pdfUrl = (c.getPdfFilename() != null)
                ? "http://localhost:8888/uploads/contracts/" + c.getPdfFilename()
                : null;

        // Thông tin đại lý
        String dealerManagerName = null;
        String dealerPhone = null;
        String dealerEmail = null;
        if (c.getDealer() != null) {
            dealerManagerName = c.getDealer().getContactPerson();
            dealerPhone = c.getDealer().getPhone();
            dealerEmail = c.getDealer().getDealerEmail();
        }

        // Thông tin hãng - Lấy từ contact person trong Manufacturer entity
        String manufacturerAdminName = null;
        String manufacturerAdminPhone = null;
        String manufacturerAdminEmail = null;
        if (c.getManufacturer() != null) {
            manufacturerAdminName = c.getManufacturer().getContactPerson();
            manufacturerAdminPhone = c.getManufacturer().getContactPersonPhone();
            manufacturerAdminEmail = c.getManufacturer().getContactPersonEmail();
        }

        return ManufacturerContractResponse.builder()
                .orderId(c.getOrder() != null ? c.getOrder().getOrderId() : null)
                .id(c.getId())
                .contractCode(c.getContractCode())
                // Thông tin đại lý
                .dealerId(c.getDealer() != null ? c.getDealer().getDealerId() : null)
                .dealerName(c.getDealer() != null ? c.getDealer().getDealerName() : null)
                .dealerManagerName(dealerManagerName)
                .dealerPhone(dealerPhone)
                .dealerEmail(dealerEmail)
                // Thông tin hãng
                .manufacturerName(c.getManufacturer() != null ? c.getManufacturer().getManufacturerName() : null)
                .manufacturerAdminName(manufacturerAdminName)
                .manufacturerAdminPhone(manufacturerAdminPhone)
                .manufacturerAdminEmail(manufacturerAdminEmail)
                // Thông tin xe
                .vehicleModel(c.getVehicleModel())
                .vehicleVersion(c.getVehicleVersion())
                .colorName(c.getColorName())
                .totalPrice(c.getTotalPrice())
                .discountRate(c.getDiscountRate())
                .terms(c.getTerms())
                .status(c.getStatus() != null ? c.getStatus().name() : null)
                .manufacturerNote(c.getManufacturerNote())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .pdfUrl(pdfUrl)
                .pdfUploadedAt(c.getPdfUploadedAt())
                .build();
    }

    @Override
    public CustomerContractResponse toCustomerContractResponse(Contract c) {
        if (c == null) return null;

        Order order = c.getOrder();
        if (order == null) return null;

        Customer customer = order.getCustomer();
        Dealer dealer = c.getDealer();
        Vehicle vehicle = order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                ? order.getOrderItems().get(0).getVehicle()
                : null;

        // Tính toán chi phí
        BigDecimal totalPrice = order.getTotalPrice();
        BigDecimal depositAmount = totalPrice
                .multiply(new BigDecimal("0.07"))
                .setScale(0, java.math.RoundingMode.HALF_UP);
        BigDecimal remainingAmount = totalPrice.subtract(depositAmount);

        String pdfUrl = (c.getPdfFilename() != null)
                ? "http://localhost:8888/uploads/contracts/" + c.getPdfFilename()
                : null;

        return CustomerContractResponse.builder()
                .id(c.getId())
                .contractCode(c.getContractCode())
                .orderId(order.getOrderId())
                // Thông tin khách hàng
                .customerId(customer != null ? customer.getCustomerId() : null)
                .customerName(customer != null ? customer.getFullName() : null)
                .customerEmail(customer != null ? customer.getEmail() : null)
                .customerPhone(customer != null ? customer.getPhone() : null)
                .customerAddress(customer != null ? customer.getAddress() : null)
                // Thông tin đại lý
                .dealerId(dealer != null ? dealer.getDealerId() : null)
                .dealerName(dealer != null ? dealer.getDealerName() : null)
                .dealerManagerName(dealer != null ? dealer.getContactPerson() : null)
                .dealerPhone(dealer != null ? dealer.getPhone() : null)
                .dealerEmail(dealer != null ? dealer.getDealerEmail() : null)
                // Thông tin xe
                .vehicleModel(vehicle != null ? vehicle.getModelName() : c.getVehicleModel())
                .vehicleVersion(vehicle != null ? vehicle.getVersion() : c.getVehicleVersion())
                .colorName(vehicle != null && vehicle.getColor() != null ? vehicle.getColor().getColorName() : c.getColorName())
                // Chi phí
                .subtotal(order.getSubtotal())
                .discountAmount(order.getTotalDiscount())
                .vatAmount(order.getVatAmount())
                .totalPrice(totalPrice)
                .depositAmount(depositAmount)
                .remainingAmount(remainingAmount)
                // Thông tin hợp đồng
                .status(c.getStatus() != null ? c.getStatus().name() : null)
                .terms(c.getTerms())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .pdfUrl(pdfUrl)
                .pdfUploadedAt(c.getPdfUploadedAt())
                .build();
    }

    @Override
    @Deprecated
    public ContractResponse toResponse(Contract c) {
        if (c == null) return null;
        String pdfUrl = (c.getPdfFilename() != null)
                ? "http://localhost:8888/uploads/contracts/" + c.getPdfFilename()
                : null;

        String dealerManagerName = null;
        if (c.getDealer() != null) {
            dealerManagerName = c.getDealer().getContactPerson();  // Lấy tên quản lý từ dealer
        }

        return ContractResponse.builder()
                .orderId(c.getOrder() != null ? c.getOrder().getOrderId() : null)
                .id(c.getId())
                .contractCode(c.getContractCode())
                .dealerId(c.getDealer() != null ? c.getDealer().getDealerId() : null)
                .dealerName(c.getDealer() != null ? c.getDealer().getDealerName() : null)
                .dealerManagerName(dealerManagerName)
                .manufacturerName(c.getManufacturer() != null ? c.getManufacturer().getManufacturerName() : null)
                .vehicleModel(c.getVehicleModel())
                .vehicleVersion(c.getVehicleVersion())
                .colorName(c.getColorName())
                .totalPrice(c.getTotalPrice())
                .discountRate(c.getDiscountRate())
                .terms(c.getTerms())
                .status(c.getStatus() != null ? c.getStatus().name() : null)
                .manufacturerNote(c.getManufacturerNote())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .pdfUrl(pdfUrl)
                .pdfUploadedAt(c.getPdfUploadedAt())
                .manufacturerSignatureData(c.getManufacturerSignature())
                .manufacturerSignedAt(c.getManufacturerSignedAt())
                .dealerSignatureData(c.getDealerSignature())
                .dealerSignedAt(c.getDealerSignedAt())
                .build();
    }


    @Override
    public ContractFileResponse toResponseFile(Contract c) {
        if (c == null) return null;

        String fileUrl = (c.getPdfFilename() != null)
                ? "http://localhost:8888/uploads/contracts/" + c.getPdfFilename()
                : null;

        return ContractFileResponse.builder()
                .contractId(c.getId())
                .contactName(c.getDealer() != null ? c.getDealer().getContactPerson() : null)
                .contactPhone(c.getDealer() != null ? c.getDealer().getPhone() : null)
                .pdfFilename(c.getPdfFilename())
                .uploadedAt(c.getPdfUploadedAt())
                .downloadUrl(fileUrl)
                .build();
    }
}
