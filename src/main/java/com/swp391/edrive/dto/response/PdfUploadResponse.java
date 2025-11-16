package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfUploadResponse {
    private Boolean success;
    private String message;
    private String pdfUrl;
}
