package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.service.ContractService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")

public class ContractController {

    private final ContractService service;

    @PostMapping
    public ResponseEntity<ContractResponse> create(@RequestBody ContractRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<ContractResponse> submit(@PathVariable Long id) {
        return ResponseEntity.ok(service.submitToManufacturer(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ContractResponse> approve(@PathVariable Long id,
                                                    @RequestParam(required = false) String note) {
        return ResponseEntity.ok(service.approve(id, note));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ContractResponse> reject(@PathVariable Long id,
                                                   @RequestParam(required = false) String note) {
        return ResponseEntity.ok(service.reject(id, note));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<List<ContractResponse>> listByDealer(@PathVariable Long dealerId) {
        return ResponseEntity.ok(service.getByDealer(dealerId));
    }

}
