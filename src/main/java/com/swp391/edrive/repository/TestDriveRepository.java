package com.swp391.edrive.repository;

import com.swp391.edrive.entity.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
    boolean existsByDealer_DealerIdAndScheduleDatetimeGreaterThanEqualAndScheduleDatetimeLessThan(
            Long dealerId, LocalDateTime startInclusive, LocalDateTime endExclusive
    );

    // nếu sau muốn lọc theo xe:
    boolean existsByDealer_DealerIdAndVehicle_VehicleIdAndScheduleDatetimeGreaterThanEqualAndScheduleDatetimeLessThan(
            Long dealerId, Long vehicleId, LocalDateTime startInclusive, LocalDateTime endExclusive
    );

    List<TestDrive> findByDealer_DealerIdAndScheduleDatetimeBetween(
            Long dealerId, LocalDateTime startInclusive, LocalDateTime endInclusive
    );

    TestDrive save(TestDrive testDrive);

    boolean existsByDealer_DealerIdAndScheduleDatetimeBetween(Long dealerId, LocalDateTime schedule, LocalDateTime end);
}
