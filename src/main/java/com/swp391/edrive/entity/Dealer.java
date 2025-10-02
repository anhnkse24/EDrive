package com.swp391.edrive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "dealers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dealerId;

    private String dealerName;
    private String address;
    private String contactPerson;
    private String phone;
    private Integer contractId;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<PricingPolicy> pricingPolicies;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<TestDrive> testDrives;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Quotation> quotations;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Contract> contracts;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<DealerDebt> dealerDebts;
}
