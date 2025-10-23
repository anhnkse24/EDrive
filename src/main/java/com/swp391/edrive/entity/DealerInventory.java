// DealerInventory.java
package com.swp391.edrive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dealer_inventory",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_inventory_dealer_versioncolor", columnNames = {"dealer_id", "version_color_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DealerInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "version_color_id", nullable = false)
    private VersionColor versionColor;

    @Column(nullable = false)
    private Integer onHand = 0;

    @Column(nullable = false)
    private Integer reserved = 0;
}
