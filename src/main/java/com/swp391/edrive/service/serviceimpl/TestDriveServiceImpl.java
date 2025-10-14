package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.TestDriveBookingRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.entity.Customer;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.TestDrive;
import com.swp391.edrive.entity.VehicleModel;
import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.TestDriveRepository;
import com.swp391.edrive.repository.VehicleRepository;
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
    private final VehicleRepository vehicleRepository;

    // Cấu hình khung giờ & rule
    private static final LocalTime OPEN = LocalTime.of(8, 0);
    private static final LocalTime CLOSE = LocalTime.of(17, 30);
    private static final Duration SLOT = Duration.ofMinutes(30);   // slot 30'
    private static final Duration MIN_AHEAD = Duration.ofHours(2); // đặt trước tối thiểu 2h
    private static final Duration CANCEL_CUTOFF = Duration.ofHours(1); // không cho hủy khi còn < 1h

    @Override
    public List<LocalTime> getAvailableSlots(Long dealerId, LocalDate date) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer không tồn tại"));

        List<LocalTime> slots = buildSlots(date);                  // 08:00, 08:30, ..., 17:00
        List<LocalTime> available = new ArrayList<>(slots);

        for (LocalTime time : slots) {
            LocalDateTime start = LocalDateTime.of(date, time);
            LocalDateTime end = start.plus(SLOT);

            boolean busy = testDriveRepository
                    .existsByDealer_DealerIdAndScheduleDatetimeGreaterThanEqualAndScheduleDatetimeLessThan(
                            dealer.getDealerId(), start, end);

            if (busy || !isBookable(start)) {
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
        VehicleModel vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Xe không tồn tại"));

        // Chỉ cho phép phút 0 hoặc 30
        if (request.getMinute() != 0 && request.getMinute() != 30) {
            throw new IllegalArgumentException("Chỉ nhận các mốc phút 00 hoặc 30 cho khung 30 phút.");
        }

        // Tìm hoặc tạo mới Customer tối thiểu từ phone/email
        Customer customer = customerRepository.findByPhone(request.getPhone())
                .or(() -> customerRepository.findByEmail(request.getEmail()))
                .orElseGet(() -> {
                    Customer c = new Customer();
                    c.setFullName(request.getFullName());
                    c.setPhone(request.getPhone());
                    c.setEmail(request.getEmail());
                    c.setAddress("N/A");
                    c.setGender("Khác");
                    c.setIdCardNo(request.getIdCardNo());
                    return customerRepository.save(c);
                });

        LocalDateTime schedule = LocalDateTime.of(
                request.getDate(),
                LocalTime.of(request.getHour(), request.getMinute())
        );

        if (!isWithinWorkingHours(schedule.toLocalTime())) {
            throw new IllegalArgumentException("Giờ hẹn ngoài khung làm việc (08:00–17:30).");
        }
        if (!isBookable(schedule)) {
            throw new IllegalArgumentException("Vui lòng đặt trước ít nhất 2 giờ.");
        }

        LocalDateTime end = schedule.plus(SLOT);
        boolean occupied = testDriveRepository
                .existsByDealer_DealerIdAndScheduleDatetimeGreaterThanEqualAndScheduleDatetimeLessThan(
                        dealer.getDealerId(), schedule, end);

        if (occupied) {
            throw new IllegalStateException("Khung giờ này đã có người đặt.");
        }

        TestDrive testDrive = new TestDrive();
        testDrive.setCustomer(customer);
        testDrive.setDealer(dealer);
        testDrive.setVehicle(vehicle);
        testDrive.setScheduleDatetime(schedule);
        testDrive.setStatus(TestDriveStatus.PENDING);

        TestDrive saved = testDriveRepository.save(testDrive);
        return toResponse(saved);
    }

    // ====== NEW: Hủy lịch ======
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
        if (LocalDateTime.now().isAfter(td.getScheduleDatetime().minus(CANCEL_CUTOFF))) {
            throw new IllegalStateException("Không thể hủy khi còn dưới 1 giờ trước giờ hẹn.");
        }

        td.setStatus(TestDriveStatus.CANCELLED);
        // Nếu bạn bổ sung field vào entity:
        // td.setCancelReason(reason);
        // td.setCancelledAt(LocalDateTime.now());

        TestDrive saved = testDriveRepository.save(td);
        return toResponse(saved);
    }

    // ====== NEW: Xem chi tiết ======
    @Override
    @Transactional(readOnly = true)
    public TestDriveResponse getById(Long testdriveId) {
        TestDrive td = testDriveRepository.findById(testdriveId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch lái thử không tồn tại."));
        return toResponse(td);
    }

    // ===== Helpers =====
    private boolean isWithinWorkingHours(LocalTime time) {
        return !time.isBefore(OPEN) && !time.isAfter(CLOSE.minus(SLOT));
    }

    private boolean isBookable(LocalDateTime schedule) {
        return schedule.isAfter(LocalDateTime.now().plus(MIN_AHEAD));
    }

    // Tạo danh sách slot 30' từ 08:00 đến 17:00
    private List<LocalTime> buildSlots(LocalDate date) {
        List<LocalTime> list = new ArrayList<>();
        for (LocalTime t = OPEN; !t.isAfter(CLOSE.minus(SLOT)); t = t.plusMinutes(30)) {
            list.add(t);
        }
        return list;
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
    public List<TestDriveResponse> listByDealer(Long dealerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestDrive> p = testDriveRepository.findByDealer_DealerId(dealerId, pageable);
        return p.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDriveResponse> listByDealerAndDate(Long dealerId, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var start = date.atStartOfDay();
        var end = date.plusDays(1).atStartOfDay(); // [00:00, 24:00) của ngày đó
        Page<TestDrive> p = testDriveRepository
                .findByDealer_DealerIdAndScheduleDatetimeBetween(dealerId, start, end, pageable);
        return p.stream().map(this::toResponse).toList();
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
            throw new IllegalStateException("Lịch đã được đánh dấu hoàn thành trước đó.");
        }

        // Optional: chỉ cho phép hoàn thành sau thời gian hẹn
        if (LocalDateTime.now().isBefore(td.getScheduleDatetime())) {
            throw new IllegalStateException("Không thể hoàn thành trước thời gian hẹn.");
        }

        td.setStatus(TestDriveStatus.COMPLETED);
        td.setCompletedAt(LocalDateTime.now());
        TestDrive saved = testDriveRepository.save(td);

        return toResponse(saved);
    }

    private TestDriveResponse toResponse(TestDrive td) {
        return new TestDriveResponse(
                td.getTestdriveId(),
                td.getCustomer().getCustomerId(),
                td.getDealer().getDealerId(),
                td.getVehicle().getVehicleId(),
                td.getScheduleDatetime(),
                td.getStatus()
        );
    }
}
