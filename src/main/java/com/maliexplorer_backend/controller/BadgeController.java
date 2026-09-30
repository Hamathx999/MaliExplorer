package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.HistoriquePointResponseDTO;
import com.maliexplorer_backend.dto.ProgressionResponseDTO;
import com.maliexplorer_backend.service.BadgeProgressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(name = "Badges & Progression (Bambara)", description = "Endpoints de gestion des points et des badges Bambara (Kalanden, Fasoden, Fasoden Yuman)")
public class BadgeController {

    private final BadgeProgressionService badgeProgressionService;

    @Operation(summary = "Obtenir mon badge et ma progression", description = "Retourne le niveau Bambara actuel (Kalanden, Fasoden, Fasoden Yuman), les points totaux, le pourcentage et les points restants pour le prochain badge.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<ProgressionResponseDTO> getMaProgression() {
        ProgressionResponseDTO progression = badgeProgressionService.getProgressionCurrentUtilisateur();
        return ResponseEntity.ok(progression);
    }

    @Operation(summary = "Obtenir mon historique de points", description = "Retourne la liste chronologique de toutes les actions ayant rapporté des points à l'utilisateur connecté.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me/history")
    public ResponseEntity<List<HistoriquePointResponseDTO>> getMonHistorique() {
        List<HistoriquePointResponseDTO> historique = badgeProgressionService.getHistoriquePointsCurrentUtilisateur();
        return ResponseEntity.ok(historique);
    }

    @Operation(summary = "Obtenir la progression d'un utilisateur (Admin)", description = "Permet aux administrateurs de consulter les points et badges de n'importe quel utilisateur.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProgressionResponseDTO> getProgressionUtilisateur(@PathVariable int userId) {
        ProgressionResponseDTO progression = badgeProgressionService.getProgressionUtilisateur(userId);
        return ResponseEntity.ok(progression);
    }

    @Operation(summary = "Obtenir l'historique d'un utilisateur (Admin)", description = "Permet aux administrateurs de consulter l'historique des points d'un utilisateur donné.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<HistoriquePointResponseDTO>> getHistoriqueUtilisateur(@PathVariable int userId) {
        List<HistoriquePointResponseDTO> historique = badgeProgressionService.getHistoriquePoints(userId);
        return ResponseEntity.ok(historique);
    }
}
