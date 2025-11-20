package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status_order_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusOrderCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @Column(nullable = false)
    private String status;

    private String deliveryDate; // ví dụ: "2025-11-10" hoặc "Chưa hẹn"
    private String deliveryLocation;

    @OneToOne
    @JoinColumn(name = "order_cus_id", nullable = false)
    private OrderCustomer orderCustomer;
}
