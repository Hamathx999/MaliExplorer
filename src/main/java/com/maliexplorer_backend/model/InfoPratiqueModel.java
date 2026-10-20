package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant une rubrique d'informations pratiques pour le voyageur au Mali.
 * Regroupe les conseils, visas, recommandations sanitaires, devises, climat et contacts d'urgence.
 */
@Entity
@Table(name = "infos_pratiques")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoPratiqueModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre de la section (ex: "1. Visas & Formalités d'Entrée")
    @Column(nullable = false, length = 150)
    private String titre;

    // Catégorie thématique (ex: "VISAS", "SANTE", "DEVISES", "CLIMAT", "URGENCES")
    @Column(nullable = false, length = 100)
    private String categorie;

    // Description synthétique ou résumé de la rubrique
    @Column(columnDefinition = "TEXT")
    private String description;

    // Conseils détaillés, règles ou puces d'information (séparées par des sauts de ligne)
    @Column(columnDefinition = "TEXT")
    private String conseils;

    // Coordonnées, adresses ou numéros de téléphone d'urgence
    @Column(columnDefinition = "TEXT")
    private String coordonnees;

    // Texte du badge d'information rapide (ex: "Obligatoire", "FCFA (XOF)", "24h/24 & 7j/7")
    @Column(length = 100)
    private String badge;

    // Code couleur hexadécimal du badge (ex: "#C53030", "#DD6B20", "#2E7D32", "#3182CE")
    @Column(length = 30)
    private String badgeColor;

    // Identifiant d'icône (ex: "badge", "medical_services", "payments", "wb_sunny", "contact_phone")
    @Column(length = 50)
    private String icon;

    // URL de l'illustration hébergée sur Supabase Storage
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // Ordre d'affichage dans la liste
    @Column(nullable = false)
    @Builder.Default
    private Integer ordreAffichage = 0;

    // Statut de visibilité publique
    @Column(nullable = false)
    @Builder.Default
    private Boolean publie = true;

    // Horodatage de création
    @CreationTimestamp
    private LocalDateTime dateCreation;

    // Horodatage de dernière modification
    @UpdateTimestamp
    private LocalDateTime dateModification;
}
