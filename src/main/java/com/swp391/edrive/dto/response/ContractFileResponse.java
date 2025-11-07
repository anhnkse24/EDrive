package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractFileResponse {
    private Long contractId;
    private String pdfFilename;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}