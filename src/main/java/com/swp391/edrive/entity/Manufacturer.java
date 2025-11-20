package com.swp391.edrive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "manufacturers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manufacturerId;

    @Column(nullable = false, length = 100)
    private String manufacturerName;

    @Column(length = 200)
    private String address;

    @Column(nullable = false, length = 100)
    private String contactPerson;

    @Column(nullable = false, length = 20)
    private String phone;

    @OneToMany(mappedBy = "manufacturer")
    @JsonIgnore
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "manufacturer")
    @JsonIgnore
    private List<ManufacturerInventory> manufacturerInventories;

    @OneToMany(mappedBy = "manufacturer")
    @JsonIgnore
    private List<Contract> contracts;
}