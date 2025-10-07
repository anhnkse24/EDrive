package com.swp391.edrive.repository;

import com.swp391.edrive.entity.TestDrive;
import com.swp391.edrive.enums.TestDriveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
    boolean existsByDealer_DealerIdAndScheduleDatetimeGreaterThanEqualAndScheduleDatetimeLessThan(
            Long dealerId, LocalDateTime startInclusive, LocalDateTime endExclusive
    );

    TestDrive save(TestDrive testDrive);

    Page<TestDrive> findAll(Pageable pageable);

    Page<TestDrive> findByDealer_DealerId(Long dealerId, Pageable pageable);

    Page<TestDrive> findByDealer_DealerIdAndScheduleDatetimeBetween(
            Long dealerId, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

    Page<TestDrive> findByStatus(TestDriveStatus status, Pageable pageable);

}
