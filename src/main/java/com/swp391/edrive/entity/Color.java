package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "colors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long colorId;

    @Column(nullable = false, unique = true, length = 50)
    private String colorName;

    @Column(length = 7) // ví dụ "#FFFFFF"
    private String hexCode;

    @OneToMany(mappedBy = "color")
    private List<Vehicle> vehicles;
}
