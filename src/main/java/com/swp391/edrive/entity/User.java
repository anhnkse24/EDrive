package com.swp391.edrive.entity;

import com.swp391.edrive.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
}
