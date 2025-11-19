package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @OneToOne(mappedBy = "orderCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    private StatusOrderCustomer statusOrderCustomer;

    public void setStatusOrderCustomer(StatusOrderCustomer statusOrderCustomer) {
        this.statusOrderCustomer = statusOrderCustomer;
        if (statusOrderCustomer != null) {
            statusOrderCustomer.setOrderCustomer(this);
        }
    }
}
