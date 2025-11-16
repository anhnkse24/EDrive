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

    private String fullName;        // Họ và tên
    private String username;        // Tên đăng nhập
    private String email;           // Email
    private String phoneNumber;     // Số điện thoại
    private String agencyName;      // Tên đại lý
    private String contactPerson;   // Người liên hệ
    private String agencyPhone;     // SĐT đại lý
    private String streetAddress;   // Số nhà và tên đường
    private String ward;            // Phường/Xã
    private String district;        // Quận/Huyện
    private String city;            // Tỉnh/Thành phố
    private String fullAddress;     // Địa chỉ đầy đủ

    // Quan hệ (tuỳ chọn) nếu profile thuộc về một Dealer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
}
