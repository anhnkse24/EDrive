package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SignContractRequest {
    // Cho phép client truyền vào thời điểm ký (nếu cần). Nếu null -> server set now()
    public LocalDateTime signedAt;
}
