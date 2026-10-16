package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant la répartition et le taux de présence d'une catégorie patrimoniale
 * pour le widget des contenus les plus consultés du tableau de bord.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenuCategorieDTO {

    /** Libellé lisible de la catégorie (ex : "Lieux historiques", "Villes", "Plats", "Articles", "Quiz") */
    private String categorie;

    /** Code technique normalisé de la catégorie (ex : "LIEUX", "VILLES", "PLATS", "ARTICLES", "QUIZ") */
    private String code;

    /** Volume total d'entités enregistrées dans cette catégorie */
    private long total;

    /** Pourcentage réel calculé sur le total consolidé des catégories représentées */
    private double pourcentage;

    /** Couleur thématique hexadécimale issue de la charte graphique MaliExplorer */
    private String couleur;
}
