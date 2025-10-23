package com.swp391.edrive.repository;

import com.swp391.edrive.entity.TestDrive;
import com.swp391.edrive.enums.TestDriveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
    // đã có của bạn:
    Page<TestDrive> findByDealer_DealerId(Long dealerId, Pageable pageable);
    Page<TestDrive> findByDealer_DealerIdAndScheduledAtBetween(Long dealerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    long countByDealer_DealerIdAndVersion_IdAndScheduledAtGreaterThanEqualAndScheduledAtLessThan(
            Long dealerId, Long versionId, LocalDateTime start, LocalDateTime end);
    long countByDealer_DealerIdAndVersionColor_IdAndScheduledAtGreaterThanEqualAndScheduledAtLessThan(
            Long dealerId, Long versionColorId, LocalDateTime start, LocalDateTime end);

    // mới cho lọc trạng thái:
    Page<TestDrive> findByStatus(TestDriveStatus status, Pageable pageable);
    Page<TestDrive> findByDealer_DealerIdAndStatus(Long dealerId, TestDriveStatus status, Pageable pageable);
    Page<TestDrive> findByDealer_DealerIdAndScheduledAtBetweenAndStatus(Long dealerId, LocalDateTime start, LocalDateTime end, TestDriveStatus status, Pageable pageable);

    // cho scheduler no-show
    List<TestDrive> findAllByStatusAndScheduledAtBefore(TestDriveStatus status, LocalDateTime before);

}
