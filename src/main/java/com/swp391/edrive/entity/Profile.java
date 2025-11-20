package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private String agencyName;
    private String contactPerson;
    private String agencyPhone;
    private String streetAddress;
    private String ward;
    private String district;
    private String city;
    private String fullAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
}
