package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImage;

    @Column(nullable = false, length = 255)
    private String nomFichier;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(length = 100)
    private String typeMime;

    private Long taille;

    @Column(length = 50)
    private String entiteType;

    private Long entiteId;

    @Builder.Default
    private LocalDateTime dateUpload = LocalDateTime.now();
}

