package com.swp391.edrive.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @NotBlank(message = "Nội dung phản hồi không được để trống")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Min(value = 1, message = "Điểm đánh giá thấp nhất là 1")
    @Max(value = 5, message = "Điểm đánh giá cao nhất là 5")
    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "test_drive_id")
    private TestDrive testDrive;
}
