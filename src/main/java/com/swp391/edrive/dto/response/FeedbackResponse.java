package com.swp391.edrive.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponse {
    private Long feedbackId;
    private Long customerId;
    private Long dealerId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
