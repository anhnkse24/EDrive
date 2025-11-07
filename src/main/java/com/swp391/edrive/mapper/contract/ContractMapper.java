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

        return ContractResponse.builder()
                .orderId(c.getOrder() != null ? c.getOrder().getOrderId() : null)
                .id(c.getId())
                .contractCode(c.getContractCode())
                .dealerId(c.getDealer() != null ? c.getDealer().getDealerId() : null)
                .dealerName(c.getDealer() != null ? c.getDealer().getDealerName() : null)
                .manufacturerName(c.getManufacturer() != null ? c.getManufacturer().getManufacturerName() : null)
                .vehicleModel(c.getVehicleModel())
                .vehicleVersion(c.getVehicleVersion())
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
    public ContractFileResponse toResponseFile(Contract c) {
        if (c == null) return null;

        String fileUrl = (c.getPdfFilename() != null)
                ? "http://localhost:8888/uploads/contracts/" + c.getPdfFilename()
                : null;

        return ContractFileResponse.builder()
                .contractId(c.getId())
                .pdfFilename(c.getPdfFilename())
                .uploadedAt(c.getPdfUploadedAt())
                .downloadUrl(fileUrl)
                .build();
    }
}
