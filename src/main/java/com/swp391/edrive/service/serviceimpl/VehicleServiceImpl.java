package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.VehicleUpsertRequest;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.ColorRepository;
import com.swp391.edrive.repository.ManufacturerInventoryRepository;
import com.swp391.edrive.repository.ManufacturerRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final ColorRepository colorRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerInventoryRepository manufacturerInventoryRepository;

    @Override
    public List<VehicleResponse> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public VehicleResponse findVehicleById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        return toResponse(v);
    }

    @Override
    public List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByStatus(status, pageable);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByColor(String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByColor_ColorNameIgnoreCaseContaining(color, pageable);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByManufactureYear(year, pageable);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYearRange(Integer fromYear, Integer toYear, int page, int size) {
        if (fromYear == null || toYear == null) {
            throw new IllegalArgumentException("fromYear and toYear are required");
        }
        if (fromYear > toYear) {
            throw new IllegalArgumentException("fromYear must be <= toYear");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByManufactureYearBetween(fromYear, toYear, pageable);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (minPrice == null && maxPrice == null) {
            throw new IllegalArgumentException("At least one of minPrice or maxPrice must be provided");
        }
        Page<Vehicle> result;
        if (minPrice != null && maxPrice != null) {
            if (minPrice.compareTo(maxPrice) > 0)
                throw new IllegalArgumentException("minPrice must be <= maxPrice");
            result = vehicleRepository.findByPriceRetailBetween(minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            result = vehicleRepository.findByPriceRetailGreaterThanEqual(minPrice, pageable);
        } else {
            result = vehicleRepository.findByPriceRetailLessThanEqual(maxPrice, pageable);
        }
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public List<VehicleResponse> createVehicle(VehicleUpsertRequest req) {
        List<VehicleResponse> responses = new ArrayList<>();

        for (var colorImage : req.getColors()) {
            boolean exists = vehicleRepository.existsByVersionIgnoreCaseAndColor_ColorId(
                    req.getVersion().trim(),
                    colorImage.getColorId()
            );
            if (exists) {
                throw new IllegalArgumentException(
                        "Xe đã tồn tại với phiên bản '" + req.getVersion() + "' và màu ID " + colorImage.getColorId()
                );
            }

            Color color = colorRepository.findById(colorImage.getColorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mã màu không tồn tại: " + colorImage.getColorId()
                    ));

            Vehicle v = new Vehicle();
            apply(v, req);

            v.setColor(color);
            v.setImageUrl(colorImage.getImageUrl());

            v = vehicleRepository.save(v);

            responses.add(toResponse(v));
        }

        return responses;
    }

    @Override
    @Transactional
    public List<VehicleResponse> createVehicleWithImages(VehicleUpsertRequest req, List<MultipartFile> images) {
        // Validate số lượng ảnh phải khớp với số màu
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ảnh không được để trống");
        }

        if (images.size() != req.getColors().size()) {
            throw new IllegalArgumentException(
                "Số lượng ảnh (" + images.size() + ") phải khớp với số màu (" + req.getColors().size() + ")"
            );
        }

        List<VehicleResponse> responses = new ArrayList<>();

        for (int i = 0; i < req.getColors().size(); i++) {
            var colorImage = req.getColors().get(i);
            MultipartFile image = images.get(i);

            // Validate ảnh
            validateImage(image);

            // Kiểm tra trùng lặp
            boolean exists = vehicleRepository.existsByVersionIgnoreCaseAndColor_ColorId(
                    req.getVersion().trim(),
                    colorImage.getColorId()
            );
            if (exists) {
                throw new IllegalArgumentException(
                        "Xe đã tồn tại với phiên bản '" + req.getVersion() + "' và màu ID " + colorImage.getColorId()
                );
            }

            Color color = colorRepository.findById(colorImage.getColorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mã màu không tồn tại: " + colorImage.getColorId()
                    ));

            Vehicle v = new Vehicle();
            apply(v, req);
            v.setColor(color);

            // Upload và set image URL
            String imageUrl = saveImage(image, req.getVersion(), color.getColorName());
            v.setImageUrl(imageUrl);

            v = vehicleRepository.save(v);
            responses.add(toResponse(v));
        }

        return responses;
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }

        String fileName = image.getOriginalFilename();
        if (fileName == null || (!fileName.toLowerCase().endsWith(".jpg")
                && !fileName.toLowerCase().endsWith(".jpeg")
                && !fileName.toLowerCase().endsWith(".png")
                && !fileName.toLowerCase().endsWith(".webp"))) {
            throw new IllegalArgumentException("Định dạng file không hợp lệ. Chỉ chấp nhận JPG, JPEG, PNG hoặc WEBP");
        }

        if (image.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file vượt quá giới hạn tối đa 10MB");
        }
    }

    private String saveImage(MultipartFile image, String version, String colorName) {
        try {
            String originalFileName = image.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = version.replaceAll("\\s+", "_") + "_"
                    + colorName.replaceAll("\\s+", "_") + "_"
                    + System.currentTimeMillis() + fileExtension;

            // Xác định đường dẫn upload
            String uploadDirPath;
            if (uploadDir != null && !uploadDir.isEmpty() && !uploadDir.equals("uploads/vehicles")) {
                uploadDirPath = uploadDir;
            } else {
                // Sử dụng đường dẫn tuyệt đối từ thư mục project
                String projectPath = System.getProperty("user.dir");
                uploadDirPath = projectPath + File.separator + "uploads" + File.separator + "vehicles";
            }

            // Tạo thư mục nếu chưa tồn tại
            File uploadDirFile = new File(uploadDirPath);
            if (!uploadDirFile.exists()) {
                boolean created = uploadDirFile.mkdirs();
                if (!created) {
                    throw new RuntimeException("Không thể tạo thư mục upload: " + uploadDirPath);
                }
            }

            // Lưu file
            File uploadFile = new File(uploadDirFile, uniqueFileName);
            image.transferTo(uploadFile);

            // Trả về đường dẫn tương đối với forward slash (/) cho URL
            return "uploads/vehicles/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload file: " + e.getMessage(), e);
        }
    }


    // === UPDATE ===
    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleUpsertRequest req) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        apply(v, req);

        if (req.getColors() != null && !req.getColors().isEmpty()) {
            var colorImage = req.getColors().get(0);
            Color color = colorRepository.findById(colorImage.getColorId())
                    .orElseThrow(() -> new IllegalArgumentException("Mã màu không tồn tại: " + colorImage.getColorId()));
            v.setColor(color);
            v.setImageUrl(colorImage.getImageUrl());
        }

        v = vehicleRepository.save(v);
        return toResponse(v);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        vehicleRepository.delete(v);
    }

    // === mapping helpers ===
    private void apply(Vehicle v, VehicleUpsertRequest r) {
        v.setModelName(r.getModelName());
        v.setVersion(r.getVersion());

        v.setBatteryCapacityKwh(r.getBatteryCapacityKwh());
        v.setRangeKm(r.getRangeKm());
        v.setMaxSpeedKmh(r.getMaxSpeedKmh());
        v.setChargingTimeHours(r.getChargingTimeHours());
        v.setSeatingCapacity(Integer.valueOf(r.getSeatingCapacity()));
        v.setMotorPowerKw(r.getMotorPowerKw());
        v.setWeightKg(r.getWeightKg());
        v.setLengthMm(r.getLengthMm());
        v.setWidthMm(r.getWidthMm());
        v.setHeightMm(r.getHeightMm());
        v.setPriceRetail(r.getPriceRetail());
        v.setStatus(r.getStatus());
        v.setManufactureYear(r.getManufactureYear());
    }

    private VehicleResponse toResponse(Vehicle v) {
        return VehicleResponse.builder()
                .vehicleId(v.getVehicleId())
                .modelName(v.getModelName())
                .version(v.getVersion())
                .color(v.getColor() != null ? v.getColor().getColorName() : null)
                .batteryCapacityKwh(v.getBatteryCapacityKwh())
                .rangeKm(v.getRangeKm())
                .maxSpeedKmh(v.getMaxSpeedKmh())
                .chargingTimeHours(v.getChargingTimeHours())
                .seatingCapacity(v.getSeatingCapacity())
                .motorPowerKw(v.getMotorPowerKw())
                .weightKg(v.getWeightKg())
                .lengthMm(v.getLengthMm())
                .widthMm(v.getWidthMm())
                .heightMm(v.getHeightMm())
                .priceRetail(v.getPriceRetail())
                .imageUrl(v.getImageUrl())
                .status(v.getStatus() != null ? v.getStatus().name() : null)
                .manufactureYear(v.getManufactureYear())
                .build();
    }

    public BigDecimal calculateDiscountedPrice(Vehicle vehicle) {
        BigDecimal basePrice = vehicle.getPriceRetail();
        BigDecimal discountedPrice = basePrice;

        if (vehicle.getPromotions() == null || vehicle.getPromotions().isEmpty()) {
            return basePrice;
        }

        LocalDate now = LocalDate.now();

        // Lọc ra các khuyến mãi còn hiệu lực
        Set<Promotion> activePromotions = vehicle.getPromotions().stream()
                .filter(p -> p.getStartDate() != null && p.getEndDate() != null)
                .filter(p -> !now.isBefore(p.getStartDate()) && !now.isAfter(p.getEndDate()))
                .collect(Collectors.toSet());

        for (Promotion promo : activePromotions) {
            if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
                // Giảm theo %
                double percent = promo.getDiscountValue() / 100.0;
                discountedPrice = discountedPrice.multiply(BigDecimal.valueOf(1 - percent));
            } else if (promo.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                // Giảm số tiền cố định
                discountedPrice = discountedPrice.subtract(BigDecimal.valueOf(promo.getDiscountValue()));
            }
        }

        // Không để giá âm
        return discountedPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discountedPrice;
    }

    @Value("${upload.vehicle-images-dir:uploads/vehicles}")
    private String uploadDir;

    @Override
    @Transactional
    public VehicleResponse uploadVehicleImage(Long vehicleId, MultipartFile image) {
        // Tìm xe theo ID
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + vehicleId));

        // Validate file
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }

        String originalFileName = image.getOriginalFilename();
        if (originalFileName == null || (!originalFileName.toLowerCase().endsWith(".jpg")
                && !originalFileName.toLowerCase().endsWith(".jpeg")
                && !originalFileName.toLowerCase().endsWith(".png")
                && !originalFileName.toLowerCase().endsWith(".webp"))) {
            throw new IllegalArgumentException("Định dạng file không hợp lệ. Chỉ chấp nhận JPG, JPEG, PNG hoặc WEBP");
        }

        if (image.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file vượt quá giới hạn tối đa 10MB");
        }

        try {
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = "vehicle_" + vehicleId + "_" + System.currentTimeMillis() + fileExtension;

            // Xác định đường dẫn upload
            String uploadDirPath;
            if (uploadDir != null && !uploadDir.isEmpty() && !uploadDir.equals("uploads/vehicles")) {
                uploadDirPath = uploadDir;
            } else {
                // Sử dụng đường dẫn tuyệt đối từ thư mục project
                String projectPath = System.getProperty("user.dir");
                uploadDirPath = projectPath + File.separator + "uploads" + File.separator + "vehicles";
            }

            File uploadDirFile = new File(uploadDirPath);

            if (!uploadDirFile.exists()) {
                boolean created = uploadDirFile.mkdirs();
                if (!created) {
                    throw new RuntimeException("Không thể tạo thư mục upload: " + uploadDirPath);
                }
            }

            File uploadFile = new File(uploadDirFile, uniqueFileName);
            image.transferTo(uploadFile);

            // Cập nhật imageUrl cho xe với đường dẫn tương đối sử dụng forward slash cho URL
            String imagePath = "uploads/vehicles/" + uniqueFileName;
            vehicle.setImageUrl(imagePath);
            vehicle = vehicleRepository.save(vehicle);

            // Trả về thông tin xe đã được cập nhật
            return toResponse(vehicle);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload file: " + e.getMessage(), e);
        }
    }
}
