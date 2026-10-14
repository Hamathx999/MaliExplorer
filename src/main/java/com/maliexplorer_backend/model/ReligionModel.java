package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "religions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReligionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 100)
    private String categorie; // ex: Islam, Christianisme, Religions traditionnelles africaines

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String histoire;

    @Column(columnDefinition = "TEXT")
    private String pratiques;

    @Column(columnDefinition = "TEXT")
    private String fetes;

    @Column(columnDefinition = "TEXT")
    private String lieuxDeCulte;

    @Column(columnDefinition = "TEXT")
    private String regionsConcernees;

    // Branches & courants documentés (format JSON ou texte structuré)
    @Column(columnDefinition = "TEXT")
    private String branches;

    // Statistiques nationales rigoureuses
    @Column(columnDefinition = "TEXT")
    private String proportionPopulation; // ex: ~94.8%

    @Column(columnDefinition = "TEXT")
    private String nombrePratiquantsEstime; // ex: ~20.5 millions

    @Column(columnDefinition = "TEXT")
    private String sourceStatistiques; // ex: Recensement Général RGPH-5 (INSTAT Mali 2022) / US State Dept Report 2023

    @Column(length = 100)
    private String anneeStatistiques; // ex: 2022

    @Column(columnDefinition = "TEXT")
    private String methodologieStatistiques; // ex: Recensement exhaustif de la population et de l'habitat

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer ordre = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean publie = true;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
}

