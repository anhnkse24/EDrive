package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.NotificationService;
import com.swp391.edrive.service.TestDriveService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        if (testDrives.isEmpty()) {
            throw new EntityNotFoundException("Không có lịch lái thử nào thuộc Dealer ID: " + dealerId);
        }
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

        TestDrive testDrive = new TestDrive(
                customer,
                dealer,
                vehicle,
                request.getScheduleDatetime(),
                request.getStatus() != null ? request.getStatus() : TestDriveStatus.PENDING
        );

        testDrive.setCancelReason(request.getCancelReason());
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

        if (request.getScheduleDatetime() != null)
            testDrive.setScheduleDatetime(request.getScheduleDatetime());
        if (request.getStatus() != null)
            testDrive.setStatus(request.getStatus());
        if (request.getCancelReason() != null)
            testDrive.setCancelReason(request.getCancelReason());

        testDriveRepository.save(testDrive);
        return mapToResponse(testDrive);
    }

    @Override
    public void deleteTestDriveByDealer(Long dealerId, Long testDriveId) {
        TestDrive testDrive = testDriveRepository.findById(testDriveId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));

        if (!testDrive.getDealer().getDealerId().equals(dealerId))
            throw new EntityNotFoundException("Không có quyền xóa lịch lái thử của Dealer khác");

        testDriveRepository.delete(testDrive);
    }

    private TestDriveResponse mapToResponse(TestDrive testDrive) {
        return TestDriveResponse.builder()
                .testdriveId(testDrive.getTestdriveId())
                .customerId(testDrive.getCustomer().getCustomerId())
                .customerName(testDrive.getCustomer().getFullName())
                .dealerId(testDrive.getDealer() != null ? testDrive.getDealer().getDealerId() : null)
                .dealerName(testDrive.getDealer() != null ? testDrive.getDealer().getDealerName() : null)
                .vehicleId(testDrive.getVehicle().getVehicleId())
                .vehicleModel(testDrive.getVehicle().getModelName())
                .scheduleDatetime(testDrive.getScheduleDatetime())
                .completedAt(testDrive.getCompletedAt())
                .status(testDrive.getStatus())
                .cancelReason(testDrive.getCancelReason())
                .build();
    }
}
