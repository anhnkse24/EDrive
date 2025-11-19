package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    private String title;
    private String message;
    private Boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private String receiverType;
}