package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryResponse {
    private String orderId;
    private String status;
    private String message;
    private LocalDateTime confirmedAt;
}
