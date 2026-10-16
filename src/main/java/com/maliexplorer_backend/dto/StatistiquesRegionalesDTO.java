package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO regroupant les statistiques territoriales globales pour les 16 régions administratives
 * officielles de la République du Mali (District de Bamako + 15 Régions).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesRegionalesDTO {

    /** Nombre total officiel de régions administratives au Mali (fixé institutionnellement à 16) */
    @Builder.Default
    private int totalRegions = 16;

    /** Nombre de régions actives (disposant d'au moins un contenu publié ou événement) */
    private int regionsActives;

    /** Taux d'activité territorial en pourcentage (ex : 75.0%) */
    private double tauxActivite;

    /** Liste détaillée des 16 pôles régionaux avec coordonnées SVG et métriques */
    private List<RegionStatDTO> regions;
}
