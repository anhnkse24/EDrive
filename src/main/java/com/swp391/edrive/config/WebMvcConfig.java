package com.swp391.edrive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối của thư mục uploads
        String projectPath = System.getProperty("user.dir");
        String uploadPath = "file:" + projectPath + File.separator + "uploads" + File.separator;

        // Cấu hình để serve static files từ /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // Cache 1 giờ

        // Cấu hình cho contracts
        registry.addResourceHandler("/uploads/contracts/**")
                .addResourceLocations(uploadPath + "contracts" + File.separator)
                .setCachePeriod(3600);

        // Cấu hình cho vehicles
        registry.addResourceHandler("/uploads/vehicles/**")
                .addResourceLocations(uploadPath + "vehicles" + File.separator)
                .setCachePeriod(3600);

        // Cấu hình cho logo
        registry.addResourceHandler("/uploads/logo/**")
                .addResourceLocations(uploadPath + "logo" + File.separator)
                .setCachePeriod(3600);
    }
}

