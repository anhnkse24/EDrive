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

    @Column(nullable = false, length = 100)
    private String dealerName;

    @Column(length = 150)
    private String houseNumberAndStreet;

    @Column(length = 100)
    private String wardOrCommune;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String provinceOrCity;

    @Column(nullable = false, length = 100)
    private String contactPerson;

    private String phone;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<TestDrive> testDrives;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<DealerInventory> dealerInventories;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Quotation> quotations;

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Contract> contracts;
}