package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.request.TestDriveStatusManagerRequest;
import com.swp391.edrive.dto.request.TestDriveStatusStaffRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.TestDriveStatusManager;
import com.swp391.edrive.enums.TestDriveStatusStaff;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.NotificationService;
import com.swp391.edrive.service.TestDriveService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestDriveServiceImpl implements TestDriveService {

    private final TestDriveRepository testDriveRepository;
    private final CustomerRepository customerRepository;
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    public List<TestDriveResponse> getAllTestDrives() {
        return testDriveRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestDriveResponse> getTestDrivesByDealerId(Long dealerId) {
        List<TestDrive> testDrives = testDriveRepository.findByDealer_DealerId(dealerId);
        return testDrives.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TestDriveResponse createTestDriveByDealer(Long dealerId, TestDriveRequest request) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đại lý"));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe"));

        User staff = userRepository.findById(request.getStaffUserId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (staff.getDealer() == null || !staff.getDealer().getDealerId().equals(dealerId)) {
            throw new RuntimeException("Nhân viên không thuộc đại lý này");
        }

        if (request.getStaffUserId() == null) {
            throw new IllegalArgumentException("StaffUserId không được để trống khi tạo mới");
        }

        boolean isDealerStaff = staff.getRoles().stream()
                .anyMatch(r -> r.getName().equals("DEALER_STAFF"));

        if (!isDealerStaff) {
            throw new RuntimeException("User không phải là staff");
        }

        TestDrive testDrive = new TestDrive(
                customer,
                dealer,
                vehicle,
                request.getScheduleDatetime(),
                TestDriveStatusManager.PENDING,
                TestDriveStatusStaff.PENDING
        );

        testDrive.setStaff(staff);

        testDriveRepository.save(testDrive);

        notificationService.createNotificationForTestDrive(dealerId, testDrive.getTestdriveId());

        return mapToResponse(testDrive);
    }

    @Override
    public TestDriveResponse updateTestDriveByDealer(Long dealerId, Long testDriveId, TestDriveRequest request) {
        TestDrive testDrive = testDriveRepository.findById(testDriveId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));

        if (!testDrive.getDealer().getDealerId().equals(dealerId))
            throw new EntityNotFoundException("Không có quyền cập nhật lịch lái thử của Dealer khác");

        testDrive.setScheduleDatetime(request.getScheduleDatetime());

        testDriveRepository.save(testDrive);
        return mapToResponse(testDrive);
    }

    @Override
    @Transactional
    public TestDriveResponse changeTestDriveStatusForManager(Long dealerId, Long testDriveId, TestDriveStatusManagerRequest request) {

        TestDrive testDrive = testDriveRepository.findById(testDriveId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));

        if (!testDrive.getDealer().getDealerId().equals(dealerId))
            throw new EntityNotFoundException("Bạn không có quyền đổi trạng thái lịch của Dealer khác");

        if (request.getStatusOfManager() == null)
            throw new IllegalArgumentException("Thiếu trạng thái");

        // ---- Cập nhật trạng thái ----
        testDrive.setStatusForManager(request.getStatusOfManager());

        if (request.getStatusOfManager() == TestDriveStatusManager.COMPLETED) {

            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(testDrive.getScheduleDatetime())) {
                throw new IllegalArgumentException("Thời gian hoàn thành không thể trước thời gian lịch hẹn.");
            }

            testDrive.setCompletedAt(now);
        }

        if (request.getStatusOfManager() == TestDriveStatusManager.CANCELLED &&
                request.getCancelReason() != null &&
                !request.getCancelReason().trim().isEmpty()) {

            testDrive.setCancelReason(request.getCancelReason());
        }

        // ---- Gửi thông báo cho Staff ----
        notificationService.createNotificationForTestDriveStatusForManager(
                testDrive,
                request,
                request.getStaffUserId()
        );
        testDriveRepository.save(testDrive);

        return mapToResponse(testDrive);
    }
    @Override
    public void deleteTestDriveByDealer(Long dealerId, Long testDriveId) {
        TestDrive testDrive = testDriveRepository.findById(testDriveId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));

        if (!testDrive.getDealer().getDealerId().equals(dealerId)) {
            throw new EntityNotFoundException("Bạn không có quyền xóa lịch của Dealer khác");
        }

        testDriveRepository.delete(testDrive);
    }

    @Override
    @Transactional
    public TestDriveResponse changeTestDriveStatusForStaff(
            Long staffUserId, Long dealerId, Long testDriveId, TestDriveStatusStaffRequest request) {

        TestDrive testDrive = testDriveRepository.findById(testDriveId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));

        if (!testDrive.getDealer().getDealerId().equals(dealerId))
            throw new EntityNotFoundException("Bạn không có quyền đổi trạng thái lịch của Dealer khác");

        if (request.getStatusOfStaff() == null)
            throw new IllegalArgumentException("Thiếu trạng thái Staff");

        if (testDrive.getStatusForManager() != TestDriveStatusManager.APPROVED) {
            throw new RuntimeException("Manager chưa phê duyệt. Staff không thể cập nhật trạng thái.");
        }

        // ---- Cập nhật ----
        testDrive.setStatusForStaff(request.getStatusOfStaff());

        // ---- Gửi thông báo cho Manager ----
        notificationService.createNotificationForTestDriveStatusForStaff(
                testDrive,
                request,
                staffUserId
        );
        testDriveRepository.save(testDrive);

        return mapToResponse(testDrive);
    }

    private TestDriveResponse mapToResponse(TestDrive testDrive) {
        return TestDriveResponse.builder()
                .testdriveId(testDrive.getTestdriveId())
                .customerId(testDrive.getCustomer().getCustomerId())
                .customerName(testDrive.getCustomer().getFullName())
                .staffId(testDrive.getStaff() != null ? testDrive.getStaff().getUserId() : null)
                .dealerId(testDrive.getDealer() != null ? testDrive.getDealer().getDealerId() : null)
                .dealerName(testDrive.getDealer() != null ? testDrive.getDealer().getDealerName() : null)
                .vehicleId(testDrive.getVehicle().getVehicleId())
                .vehicleModel(testDrive.getVehicle().getModelName())
                .scheduleDatetime(testDrive.getScheduleDatetime())
                .completedAt(testDrive.getCompletedAt())
                .statusForManager(testDrive.getStatusForManager())
                .statusForStaff(testDrive.getStatusForStaff())
                .cancelReason(testDrive.getCancelReason())
                .build();
    }
}
