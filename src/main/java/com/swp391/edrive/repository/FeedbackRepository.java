package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByCustomer_CustomerId(Long customerId, Pageable pageable);

}

