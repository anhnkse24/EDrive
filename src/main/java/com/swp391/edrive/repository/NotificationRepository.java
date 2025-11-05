package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDealer_DealerIdOrderByCreatedAtDesc(Long dealerId);
}