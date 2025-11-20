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
public class SignatureResponse {
    private Long id;
    private String orderId;
    private String status;
    private ManufacturerSignature manufacturer;
    private DealerSignature dealer;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManufacturerSignature {
        private String name;
        private String signatureData;
        private LocalDateTime signedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DealerSignature {
        private String name;
        private String signatureData;
        private LocalDateTime signedAt;
    }
}
