// VehicleModel.java
package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "vehicle_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên model xe (ví dụ: "E-Car A", "VF8") */
    @Column(name = "model_name", nullable = false, unique = true, length = 100)
    private String modelName;

    /** Mô tả ngắn */
    @Column(length = 255)
    private String description;

    /** Ảnh đại diện model */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /** Danh sách version thuộc model */
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<VehicleVersion> versions = new ArrayList<>();
}
