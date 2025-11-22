package com.swp391.edrive.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class UploadDirectoryInitializer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void initializeUploadDirectory() {
        try {
            String uploadPath;

            // Nếu config là "uploads" hoặc rỗng, dùng user home
            if (uploadDir == null || uploadDir.isBlank() || uploadDir.equals("uploads")) {
                uploadPath = System.getProperty("user.home") + File.separator + "edrive_uploads" + File.separator + "bills";
            } else {
                uploadPath = uploadDir;
            }

            // Tạo thư mục nếu chưa tồn tại
            Files.createDirectories(Paths.get(uploadPath));

            log.info(" Upload directory initialized at: {}", uploadPath);

        } catch (Exception e) {
            log.error("Failed to initialize upload directory", e);
            throw new RuntimeException("Cannot initialize upload directory", e);
        }
    }
}

