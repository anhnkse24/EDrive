package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.InventoryService;
import com.swp391.edrive.service.TestDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestDriveServiceImpl implements TestDriveService {
    private final TestDriveRepository testDriveRepository;
    private final CustomerRepository customerRepository;
    private final DealerRepository dealerRepository;
    private final VehicleVersionRepository vehicleVersionRepository;
    private final VersionColorRepository versionColorRepository;
    private final InventoryService inventoryService;

    // === Giờ làm việc & Slot ===
    private static final LocalTime OPEN = LocalTime.of(8, 0);      // 08:00
    private static final LocalTime CLOSE = LocalTime.of(17, 30);   // 17:30
    private static final Duration SLOT = Duration.ofMinutes(30);   // slot 30'

    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(Long dealerId, Long versionId, Long versionColorId, LocalDate date) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer không tồn tại"));

        VehicleVersion version = vehicleVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên bản xe không tồn tại"));

        VersionColor vc = null;
        if (versionColorId != null) {
            vc = versionColorRepository.findById(versionColorId)
                    .orElseThrow(() -> new IllegalArgumentException("Màu không tồn tại"));
            if (!vc.getVersion().getId().equals(version.getId())) {
                throw new IllegalArgumentException("Màu không thuộc phiên bản đã chọn");
            }
        }

        int capacity = (vc != null)
                ? inventoryService.getDemoCapacityByVersionColor(dealerId, vc.getId())
                : inventoryService.getDemoCapacityByVersion(dealerId, versionId);
        if (capacity <= 0) capacity = 1;

        List<LocalTime> slots = buildSlots(date);
        List<LocalTime> available = new ArrayList<>(slots);

        for (LocalTime time : slots) {
            LocalDateTime start = LocalDateTime.of(date, time);
            LocalDateTime end = start.plus(SLOT);

            long booked = (vc != null)
                    ? testDriveRepository.countByDealer_DealerIdAndVersionColor_IdAndScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                    dealer.getDealerId(), vc.getId(), start, end)
                    : testDriveRepository.countByDealer_DealerIdAndVersion_IdAndScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                    dealer.getDealerId(), version.getId(), start, end);

            // chỉ chặn khi đã đầy capacity, KHÔNG áp rule thời gian khác
            if (booked >= capacity) {
                available.remove(time);
            }
        }
        return available;
    }

    @Override
    @Transactional
    public TestDriveResponse book(TestDriveBookingRequest request) {
        Dealer dealer = dealerRepository.findById(request.getDealerId())
                .orElseThrow(() -> new IllegalArgumentException("Dealer không tồn tại"));

        VehicleVersion version = vehicleVersionRepository.findById(request.getVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Phiên bản xe không tồn tại"));

        VersionColor versionColor = null;
        if (request.getVersionColorId() != null) {
            versionColor = versionColorRepository.findById(request.getVersionColorId())
                    .orElseThrow(() -> new IllegalArgumentException("Màu không tồn tại"));
            if (!versionColor.getVersion().getId().equals(version.getId())) {
                throw new IllegalArgumentException("Màu không thuộc phiên bản đã chọn");
            }
        }

        // Chỉ chấp nhận phút 00 hoặc 30 để bám slot 30'
        if (request.getMinute() != 0 && request.getMinute() != 30) {
            throw new IllegalArgumentException("Chỉ nhận các mốc phút 00 hoặc 30 cho khung 30 phút.");
        }

        // Tìm hoặc tạo Customer
        Customer customer = customerRepository.findByPhone(request.getPhone())
                .or(() -> customerRepository.findByEmail(request.getEmail()))
                .orElseGet(() -> {
                    Customer c = new Customer();
                    c.setFullName(request.getFullName());
                    c.setPhone(request.getPhone());
                    c.setEmail(request.getEmail());
                    c.setAddress("N/A");
                    c.setGender(com.swp391.edrive.enums.Gender.KHAC);
                    c.setIdCardNo(request.getIdCardNo());
                    return customerRepository.save(c);
                });

        LocalDateTime scheduledAt = LocalDateTime.of(
                request.getDate(), LocalTime.of(request.getHour(), request.getMinute())
        );

        // ✅ CHỈ kiểm tra trong giờ làm việc
        if (!isWithinWorkingHours(scheduledAt.toLocalTime())) {
            throw new IllegalArgumentException("Giờ hẹn ngoài khung làm việc (08:00–17:30).");
        }

        LocalDateTime end = scheduledAt.plus(SLOT);

        int capacity = (versionColor != null)
                ? inventoryService.getDemoCapacityByVersionColor(dealer.getDealerId(), versionColor.getId())
                : inventoryService.getDemoCapacityByVersion(dealer.getDealerId(), version.getId());
        if (capacity <= 0) capacity = 1;

        long booked = (versionColor != null)
                ? testDriveRepository.countByDealer_DealerIdAndVersionColor_IdAndScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                dealer.getDealerId(), versionColor.getId(), scheduledAt, end)
                : testDriveRepository.countByDealer_DealerIdAndVersion_IdAndScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                dealer.getDealerId(), version.getId(), scheduledAt, end);

        if (booked >= capacity) {
            throw new IllegalStateException("Khung giờ này đã đủ số lượng lái thử cho xe đã chọn.");
        }

        inventoryService.reserveDemoVehicle(
                dealer.getDealerId(),
                version.getId(),
                (versionColor != null ? versionColor.getId() : null)
        );

        TestDrive td = new TestDrive();
        td.setCustomer(customer);
        td.setDealer(dealer);
        td.setVersion(version);
        td.setVersionColor(versionColor);
        td.setScheduledAt(scheduledAt);
        td.setStatus(TestDriveStatus.PENDING);
        td.setCustomerFullName(customer.getFullName());
        td.setCustomerIdCard(customer.getIdCardNo());
        td.setCustomerEmail(customer.getEmail());
        td.setCustomerPhone(customer.getPhone());

        TestDrive saved = testDriveRepository.save(td);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TestDriveResponse cancel(Long testdriveId, String reason) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));

        if (td.getStatus() == TestDriveStatus.CANCELLED) {
            throw new IllegalStateException("Lịch đã bị hủy trước đó.");
        }
        if (td.getStatus() == TestDriveStatus.COMPLETED) {
            throw new IllegalStateException("Lịch đã hoàn thành, không thể hủy.");
        }
        if (td.getStatus() == TestDriveStatus.IN_PROGRESS) {
            throw new IllegalStateException("Lịch đang diễn ra, không thể hủy.");
        }
        if (td.getStatus() == TestDriveStatus.NO_SHOW) {
            throw new IllegalStateException("Lịch đã no-show, không thể hủy.");
        }

        // ❌ Không còn cutoff thời gian
        td.setStatus(TestDriveStatus.CANCELLED);
        td.setCancelReason(reason);
        td.setCancelledAt(LocalDateTime.now());

        inventoryService.releaseDemoVehicle(
                td.getDealer().getDealerId(),
                td.getVersion().getId(),
                (td.getVersionColor() != null ? td.getVersionColor().getId() : null)
        );

        TestDrive saved = testDriveRepository.save(td);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TestDriveResponse getById(Long testdriveId) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));
        return toResponse(td);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestDrive> p = testDriveRepository.findAll(pageable);
        return p.stream().map(this::toResponse).toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> list(int page, int size, TestDriveStatus status) {
        if (status == null) return list(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<TestDrive> p = testDriveRepository.findAll(pageable);
        return p.stream()
                .filter(td -> td.getStatus() == status)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> listByDealer(Long dealerId, int page, int size, TestDriveStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestDrive> p = testDriveRepository.findByDealer_DealerId(dealerId, pageable);
        if (status == null) {
            return p.stream().map(this::toResponse).toList();
        }
        return p.stream()
                .filter(td -> td.getStatus() == status)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> listByDealerAndDate(Long dealerId, LocalDate date, int page, int size, TestDriveStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        var start = date.atStartOfDay();
        var end = date.plusDays(1).atStartOfDay();
        Page<TestDrive> p = testDriveRepository
                .findByDealer_DealerIdAndScheduledAtBetween(dealerId, start, end, pageable);
        if (status == null) {
            return p.stream().map(this::toResponse).toList();
        }
        return p.stream()
                .filter(td -> td.getStatus() == status)
                .map(this::toResponse)
                .toList();
    }

    // ====== Trạng thái mở rộng: confirm / check-in / no-show / complete ======

    @Override
    @Transactional
    public TestDriveResponse confirm(Long testdriveId) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));
        if (td.getStatus() != TestDriveStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể xác nhận lịch ở trạng thái PENDING.");
        }
        // ❌ Không ràng buộc “quá sát giờ hẹn”
        td.setStatus(TestDriveStatus.CONFIRMED);
        td.setConfirmedAt(LocalDateTime.now());
        return toResponse(testDriveRepository.save(td));
    }

    @Override
    @Transactional
    public TestDriveResponse checkIn(Long testdriveId) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));
        if (td.getStatus() != TestDriveStatus.CONFIRMED) {
            throw new IllegalStateException("Chỉ check-in lịch ở trạng thái CONFIRMED.");
        }
        // ❌ Không chặn check-in sớm/muộn
        td.setStatus(TestDriveStatus.IN_PROGRESS);
        td.setCheckInAt(LocalDateTime.now());
        return toResponse(testDriveRepository.save(td));
    }

    @Override
    @Transactional
    public TestDriveResponse markNoShow(Long testdriveId) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));
        if (td.getStatus() != TestDriveStatus.CONFIRMED && td.getStatus() != TestDriveStatus.PENDING) {
            throw new IllegalStateException("Chỉ đánh dấu NO_SHOW cho lịch PENDING/CONFIRMED.");
        }
        // ❌ Không cần đợi 30' sau giờ hẹn

        inventoryService.releaseDemoVehicle(
                td.getDealer().getDealerId(),
                td.getVersion().getId(),
                (td.getVersionColor() != null ? td.getVersionColor().getId() : null)
        );

        td.setStatus(TestDriveStatus.NO_SHOW);
        return toResponse(testDriveRepository.save(td));
    }

    @Override
    @Transactional
    public TestDriveResponse complete(Long testdriveId) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));

        if (td.getStatus() == TestDriveStatus.CANCELLED) {
            throw new IllegalStateException("Lịch đã bị hủy, không thể hoàn thành.");
        }
        if (td.getStatus() == TestDriveStatus.COMPLETED) {
            throw new IllegalStateException("Lịch đã hoàn thành trước đó.");
        }
        if (td.getStatus() == TestDriveStatus.NO_SHOW) {
            throw new IllegalStateException("Lịch đã no-show, không thể hoàn thành.");
        }
        if (td.getStatus() != TestDriveStatus.IN_PROGRESS) {
            throw new IllegalStateException("Chỉ hoàn thành lịch ở trạng thái IN_PROGRESS (đã check-in).");
        }

        // ❌ Không chặn “chưa tới giờ hẹn”
        td.setStatus(TestDriveStatus.COMPLETED);
        td.setCompletedAt(LocalDateTime.now());

        inventoryService.releaseDemoVehicle(
                td.getDealer().getDealerId(),
                td.getVersion().getId(),
                (td.getVersionColor() != null ? td.getVersionColor().getId() : null)
        );

        return toResponse(testDriveRepository.save(td));
    }

    // ===== Helpers =====
    private boolean isWithinWorkingHours(LocalTime time) {
        // chỉ cần nằm trong [08:00, 17:30 - SLOT]
        return !time.isBefore(OPEN) && !time.isAfter(CLOSE.minus(SLOT));
    }

    private List<LocalTime> buildSlots(LocalDate date) {
        List<LocalTime> list = new ArrayList<>();
        for (LocalTime t = OPEN; !t.isAfter(CLOSE.minus(SLOT)); t = t.plusMinutes(30)) {
            list.add(t);
        }
        return list;
    }

    private TestDriveResponse toResponse(TestDrive td) {
        TestDriveResponse r = new TestDriveResponse(
                td.getId(),
                td.getCustomer().getCustomerId(),
                td.getDealer().getDealerId(),
                td.getVersion().getId(),
                td.getVersionColor() != null ? td.getVersionColor().getId() : null,
                td.getScheduledAt(),
                td.getStatus(),
                td.getConfirmedAt(),
                td.getCheckInAt(),
                td.getCompletedAt(),
                td.getCancelledAt(),
                td.getCancelReason(),
                td.getCustomerIdCard(),
                td.getCustomerEmail(),
                td.getCustomerFullName(),
                td.getCustomerPhone()
        );
        return r;
    }
}
