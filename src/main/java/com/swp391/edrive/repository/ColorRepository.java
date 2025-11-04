package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByColorNameIgnoreCase(String colorName);
    Optional<Color> findByHexCodeIgnoreCase(String hexCode);
    List<Color> findByColorNameIgnoreCaseContaining(String keyword);
}
