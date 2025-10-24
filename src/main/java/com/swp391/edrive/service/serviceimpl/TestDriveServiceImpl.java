package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.TestDriveRequest;
import com.swp391.edrive.dto.response.TestDriveResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.repository.*;
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

    @Override
    public TestDriveResponse createTestDrive(TestDriveRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng"));
        Dealer dealer = dealerRepository.findById(request.getDealerId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đại lý"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe"));

        // Khi tạo mới => mặc định là PENDING (đang chờ xác nhận)
        TestDrive testDrive = new TestDrive(
                customer,
                dealer,
                vehicle,
                request.getScheduleDatetime(),
                request.getStatus() != null ? request.getStatus() : TestDriveStatus.PENDING
        );

        testDrive.setCancelReason(request.getCancelReason());
        testDrive.setCompletedAt(null);

        testDriveRepository.save(testDrive);
        return mapToResponse(testDrive);
    }

    @Override
    public TestDriveResponse updateTestDrive(Long id, TestDriveRequest request) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));

        if (request.getScheduleDatetime() != null)
            testDrive.setScheduleDatetime(request.getScheduleDatetime());
        if (request.getStatus() != null)
            testDrive.setStatus(request.getStatus());
        if (request.getCancelReason() != null)
            testDrive.setCancelReason(request.getCancelReason());

        // Nếu đổi trạng thái sang COMPLETED => set thời gian hoàn tất
        if (request.getStatus() == TestDriveStatus.COMPLETED)
            testDrive.setCompletedAt(LocalDateTime.now());

        testDriveRepository.save(testDrive);
        return mapToResponse(testDrive);
    }

    @Override
    public void deleteTestDrive(Long id) {
        if (!testDriveRepository.existsById(id))
            throw new EntityNotFoundException("Không tìm thấy lịch lái thử để xóa");
        testDriveRepository.deleteById(id);
    }

    @Override
    public TestDriveResponse getTestDriveById(Long id) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch lái thử"));
        return mapToResponse(testDrive);
    }

    @Override
    public List<TestDriveResponse> getAllTestDrives() {
        return testDriveRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TestDriveResponse mapToResponse(TestDrive testDrive) {
        return TestDriveResponse.builder()
                .testdriveId(testDrive.getTestdriveId())
                .customerId(testDrive.getCustomer().getCustomerId())
                .customerName(testDrive.getCustomer().getFullName())
                .dealerId(testDrive.getDealer().getDealerId())
                .dealerName(testDrive.getDealer().getDealerName()) // sửa lại đúng tên field
                .vehicleId(testDrive.getVehicle().getVehicleId())
                .vehicleModel(testDrive.getVehicle().getModelName())
                .scheduleDatetime(testDrive.getScheduleDatetime())
                .completedAt(testDrive.getCompletedAt())
                .status(testDrive.getStatus())
                .cancelReason(testDrive.getCancelReason())
                .build();
    }
}
