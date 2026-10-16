package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.DashboardStatsResponseDTO;
import com.maliexplorer_backend.service.DashboardStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour le tableau de bord administrateur MaliExplorer.
 * Fournit l'endpoint d'agrégation consolidé pour les KPIs, séries temporelles d'activité,
 * répartition des catégories patrimoniales, carte des 16 régions administratives,
 * éléments à modérer et fil chronologique d'activité récente.
 *
 * Sécurisation stricte : accès réservé exclusivement aux comptes disposant du rôle ADMIN.
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Tableau de Bord Administrateur", description = "API d'agrégation des statistiques et de modération du tableau de bord")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardStatsController {

    private final DashboardStatsService dashboardStatsService;

    /**
     * Récupère l'intégralité des statistiques consolidées du tableau de bord administrateur.
     *
     * @return 200 OK avec le DTO DashboardStatsResponseDTO alimenté par les données réelles
     */
    @GetMapping("/stats")
    @Operation(
            summary = "Consulter les statistiques consolidées du tableau de bord admin",
            description = "Retourne les 5 KPIs supérieurs, séries d'activité (7j/30j/12m), jauges de catégories, carte des 16 régions, fiches à valider et activités récentes"
    )
    public ResponseEntity<DashboardStatsResponseDTO> getStats() {
        log.info("Requête administrative reçue : GET /api/admin/dashboard/stats");
        DashboardStatsResponseDTO stats = dashboardStatsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
