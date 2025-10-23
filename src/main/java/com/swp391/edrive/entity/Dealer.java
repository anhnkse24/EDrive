package com.swp391.edrive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "dealers",
        indexes = {
                @Index(name = "idx_dealer_name", columnList = "dealer_name"),
                @Index(name = "idx_dealer_code", columnList = "dealer_code", unique = true)
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dealerId;

    @Column(name = "dealer_code", length = 32, nullable = false, unique = true)
    private String dealerCode;

    @Column(name = "dealer_name", length = 150, nullable = false)
    private String dealerName;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "phone", length = 30)
    private String phone;

    private Integer contractId;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<DealerInventory> inventories;

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
