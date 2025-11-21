package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-support") // Endpoint rõ ràng
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class GeminiChatController {

    private final GeminiService geminiService;

    @Operation(summary = "Chat với Trợ lý ảo vận hành (Dành cho Dealer Admin)")
    @PostMapping
    public ResponseEntity<ResponseObject<String>> chatWithAI(
            @RequestParam String message,
            @RequestParam Long dealerId,    // BẮT BUỘC: Để AI biết load kho của ai
            @RequestParam(required = false) String sessionId // TÙY CHỌN: Để AI nhớ lịch sử chat
    ) {
        try {
            // Nếu Frontend không gửi sessionId, tự tạo session tạm dựa trên dealerId
            String finalSessionId = (sessionId == null || sessionId.isEmpty())
                    ? "dealer_session_" + dealerId
                    : sessionId;

            String response = geminiService.chat(message, finalSessionId, dealerId);

            return ResponseEntity.ok(
                    ResponseObject.<String>builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Success")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.<String>builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("AI Error: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }
}