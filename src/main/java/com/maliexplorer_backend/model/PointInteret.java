package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "points_interet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointInteret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 50)
    private String type; // EX: NATURE, ARTISANAT, MONUMENT, CULTURE

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String panorama360Url;

    private Long regionId;
}
