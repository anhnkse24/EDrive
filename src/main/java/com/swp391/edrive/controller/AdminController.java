package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UnverifiedUserResponse;
import com.swp391.edrive.exception.exceptions.BadRequestException;
import com.swp391.edrive.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "api")
public class AdminController {

    private final AuthenticationService authenticationService;

    @GetMapping("/unverified-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all unverified accounts", 
               description = "Retrieve a list of all user accounts that have not been verified yet")
    public ResponseEntity<ResponseObject<List<UnverifiedUserResponse>>> getAllUnverifiedAccounts() {
        try {
            List<UnverifiedUserResponse> unverifiedAccounts = authenticationService.getAllUnverifiedAccounts();
            return ResponseEntity.ok()
                    .body(new ResponseObject<>(
                            HttpStatus.OK.value(),
                            "Retrieved unverified accounts successfully",
                            unverifiedAccounts));
        } catch (Exception e) {
            throw new BadRequestException("Failed to retrieve unverified accounts: " + e.getMessage(), e);
        }
    }

    @PostMapping("/verify-account/{dealerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify account by Dealer ID",
               description = "Manually verify a dealer account by providing the dealer ID. This will enable the account and send a confirmation email to the dealer.")
    public ResponseEntity<ResponseObject<Void>> verifyAccountByDealerId(@PathVariable Long dealerId) {
        try {
            authenticationService.verifyAccountByDealerId(dealerId);
            return ResponseEntity.ok()
                    .body(new ResponseObject<>(
                            HttpStatus.OK.value(),
                            "Tài khoản đại lý đã được xác nhận thành công",
                            null));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Xác nhận tài khoản thất bại: " + e.getMessage(), e);
        }
    }

    @GetMapping("/business-license/{dealerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "View/Download business license",
               description = "Download the business license file for a dealer by dealer ID")
    public ResponseEntity<Resource> getBusinessLicense(@PathVariable Long dealerId) {
        try {
            String businessLicenseUrl = authenticationService.getBusinessLicenseUrl(dealerId);

            File file = new File(businessLicenseUrl);
            if (!file.exists()) {
                throw new BadRequestException("Business license file not found");
            }

            Path path = Paths.get(businessLicenseUrl);
            Resource resource = new FileSystemResource(file);

            // Determine content type
            String contentType;
            try {
                contentType = Files.probeContentType(path);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            } catch (IOException e) {
                contentType = "application/octet-stream";
            }

            String filename = file.getName();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to retrieve business license: " + e.getMessage(), e);
        }
    }
}
