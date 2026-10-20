package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant une langue nationale du Mali.
 * Stocke le nom officiel, le nom en langue locale, les estimations de locuteurs,
 * les régions géographiques, la description culturelle et le lexique d'apprentissage (format JSON).
 */
@Entity
@Table(name = "langues_nationales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LangueNationaleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom usuel et officiel de la langue (ex: "Bamanankan (Bambara)")
    @Column(nullable = false, length = 150)
    private String nom;

    // Nom dans la langue locale originelle (ex: "Bamanankan")
    @Column(length = 150)
    private String nomLocal;

    // Présentation culturelle et historique de la langue
    @Column(columnDefinition = "TEXT")
    private String description;

    // Pourcentage ou proportion estimée de locuteurs au Mali (ex: "~80% de la population malienne")
    @Column(length = 150)
    private String locuteurs;

    // Régions d'implantation prédominantes (ex: "Bamako, Ségou, Koulikoro, Sikasso")
    @Column(length = 255)
    private String regions;

    // URL de l'illustration hébergée sur Supabase Storage
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // Contenu JSON sérialisé des expressions usuelles et du lexique de survie
    @Column(columnDefinition = "LONGTEXT")
    private String lexiqueJson;

    // Ordre de tri dans l'affichage du catalogue
    @Column(nullable = false)
    @Builder.Default
    private Integer ordreAffichage = 0;

    // Statut de visibilité publique
    @Column(nullable = false)
    @Builder.Default
    private Boolean publie = true;

    // Date de création de la fiche
    @CreationTimestamp
    private LocalDateTime dateCreation;

    // Date de dernière mise à jour
    @UpdateTimestamp
    private LocalDateTime dateModification;
}
