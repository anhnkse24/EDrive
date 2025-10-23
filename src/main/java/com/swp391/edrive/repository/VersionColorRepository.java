package com.swp391.edrive.repository;

import com.swp391.edrive.entity.VersionColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionColorRepository extends JpaRepository<VersionColor, Long> {
    // Danh sách màu của 1 version
    List<VersionColor> findByVersion_Id(Long versionId);

    // Tìm màu theo code trong 1 version
    Optional<VersionColor> findByVersion_IdAndColorCode(Long versionId, String colorCode);

    boolean existsByVersion_IdAndColorCode(Long versionId, String colorCode);

    boolean existsByVersion_IdAndColorCodeIgnoreCaseAndIdNot(Long versionId, String colorCode, Long id);

}
