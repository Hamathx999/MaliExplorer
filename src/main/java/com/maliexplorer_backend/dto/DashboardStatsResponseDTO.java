package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO racine consolidé pour la réponse de l'API de statistiques du tableau de bord administrateur
 * (endpoint REST GET /api/admin/dashboard/stats).
 * Regroupe l'intégralité des données nécessaires pour alimenter le tableau de bord moderne MaliExplorer :
 * - Les 5 cartes KPI supérieures
 * - La courbe SVG multi-périodes (7 jours, 30 jours, 12 mois)
 * - La jauge de répartition des contenus par catégorie
 * - La cartographie interactive des 16 régions administratives officielles
 * - Le tableau des éléments en attente de modération (« À valider »)
 * - Le fil chronologique de l'activité récente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponseDTO {

    /** Horodatage précis de génération des métriques statistiques */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** Les 5 compteurs KPI supérieurs avec tendances et deltas */
    private KpisDTO kpis;

    /** Séries temporelles d'activité agrégée (7j, 30j, 12m) */
    private SeriesTemporellesDTO seriesTemporelles;

    /** Répartition et pourcentages réels des contenus les plus consultés par catégorie */
    private List<ContenuCategorieDTO> contenusParCategorie;

    /** Métriques et coordonnées vectorielles des 16 régions administratives du Mali */
    private StatistiquesRegionalesDTO statistiquesRegionales;

    /** Liste des éléments nécessitant une validation administrative immédiate */
    @JsonProperty("aValider")
    private List<ElementModerationDTO> aValider;

    /** Fil chronologique des dernières actions réelles enregistrées sur la plateforme */
    private List<ActiviteRecenteDTO> activiteRecente;

    // Alias statiques internes pour compatibilité et lisibilité
    public static class Kpis extends KpisDTO {}
    public static class SeriesTemporelles extends SeriesTemporellesDTO {}
    public static class PointTemporel extends PointTemporelDTO {}
    public static class ContenuCategorie extends ContenuCategorieDTO {}
    public static class StatistiquesRegionales extends StatistiquesRegionalesDTO {}
    public static class RegionStat extends RegionStatDTO {}
    public static class ElementModeration extends ElementModerationDTO {}
    public static class ActiviteRecente extends ActiviteRecenteDTO {}
}
