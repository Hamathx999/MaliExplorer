package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.DashboardStatsResponseDTO;

/**
 * Interface du service métier pour le calcul et l'agrégation des statistiques du tableau de bord administrateur.
 * Assure la collecte des métriques sur les 14 tables de données réelles et consolide la réponse
 * pour l'interface d'administration MaliExplorer.
 */
public interface DashboardStatsService {

    /**
     * Récupère l'ensemble consolidé des indicateurs clés (KPIs), séries temporelles (7j, 30j, 12m),
     * répartition patrimoniale par catégorie, découpage des 16 régions administratives,
     * fiches en attente de validation et fil des activités récentes.
     *
     * @return Le DTO consolidé DashboardStatsResponseDTO avec données 100% réelles
     */
    DashboardStatsResponseDTO getDashboardStats();

    default java.util.List<com.maliexplorer_backend.dto.ActiviteRecenteDTO> getActivitesRecentesCompletes() {
        return java.util.Collections.emptyList();
    }
}
