package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant les statistiques culturelles et les coordonnées vectorielles
 * d'une région administrative officielle du Mali pour la carte SVG interactive.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionStatDTO {

    /** Identifiant de la région en base de données (si persistée) */
    private Long idRegion;

    /** Code officiel ou trigramme de la région (ex : "BKO", "SKO", "MOP") */
    private String code;

    /** Nom complet ou officiel de la région */
    private String nom;

    /** Chef-lieu ou capitale administrative régionale */
    private String chefLieu;

    /** Coordonnée X de projection sur le canevas SVG de la carte du Mali */
    private double x;

    /** Coordonnée Y de projection sur le canevas SVG de la carte du Mali */
    private double y;

    /** Vrai si la région possède au moins une entité culturelle ou événement associé */
    private boolean actif;

    /** Total cumulé des entités associées à cette région */
    private long totalEntites;

    /** Nombre de villes répertoriées dans cette région */
    private long nombreVilles;

    /** Nombre de lieux historiques situés dans cette région */
    private long nombreLieux;

    /** Nombre d'événements programmés dans cette région */
    private long nombreEvenements;
}
