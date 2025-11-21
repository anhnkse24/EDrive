package com.swp391.edrive.controller;

import com.swp391.edrive.service.GeminiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeminiChatController {

    private final GeminiService geminiService;

    public GeminiChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return geminiService.chat(message);
    }
}
