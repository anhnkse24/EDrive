package com.swp391.edrive.mapper.contract;

import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.entity.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper implements IContractMapper {

    @Override
    public ContractResponse toResponse(Contract c) {
        if (c == null) return null;
        String pdfUrl = (c.getPdfFilename() != null)
                ? "http://localhost:8888/uploads/contracts/" + c.getPdfFilename()
                : null;

        String userFullName = null;
        if (c.getOrder() != null && c.getOrder().getCreatedBy() != null) {
            userFullName = c.getOrder().getCreatedBy().getFullName();
        }

        return ContractResponse.builder()
                .orderId(c.getOrder() != null ? c.getOrder().getOrderId() : null)
                .id(c.getId())
                .contractCode(c.getContractCode())
                .dealerId(c.getDealer() != null ? c.getDealer().getDealerId() : null)
                .dealerName(c.getDealer() != null ? c.getDealer().getDealerName() : null)
                .userFullName(userFullName)
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
