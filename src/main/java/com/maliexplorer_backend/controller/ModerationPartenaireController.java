package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.model.StatutModeration;
import com.maliexplorer_backend.service.OpportuniteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/moderation/opportunites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Modération Admin Partenariats", description = "Validation ou rejet des projets et demandes de partenariat déposés par les partenaires")
@SecurityRequirement(name = "Bearer Authentication")
public class ModerationPartenaireController {

    private final OpportuniteService opportuniteService;

    @GetMapping
    @Operation(summary = "Lister les fiches partenaires avec filtrage optionnel par statut (par défaut EN_ATTENTE_VALIDATION)")
    public ResponseEntity<List<OpportuniteResponseDTO>> getOpportunites(
            @RequestParam(required = false) String statut) {
        StatutModeration statutEnum = parseStatutModeration(statut);
        return ResponseEntity.ok(opportuniteService.getOpportunitesParStatut(statutEnum));
    }

    @PatchMapping("/{idUsers}/valider")
    @Operation(summary = "Valider une fiche partenaire (Statut -> VALIDE)")
    public ResponseEntity<OpportuniteResponseDTO> valider(@PathVariable int idUsers) {
        return ResponseEntity.ok(opportuniteService.validerOpportunite(idUsers));
    }

    @PatchMapping("/{idUsers}/rejeter")
    @Operation(summary = "Rejeter une fiche partenaire (Statut -> REJETE) avec motif optionnel via query ou body")
    public ResponseEntity<OpportuniteResponseDTO> rejeter(
            @PathVariable int idUsers,
            @RequestParam(required = false) String motif,
            @RequestBody(required = false) Map<String, String> body) {
        String resolvedMotif = extraireMotif(motif, body);
        return ResponseEntity.ok(opportuniteService.rejeterOpportunite(idUsers, resolvedMotif));
    }

    private StatutModeration parseStatutModeration(String statut) {
        if (statut == null || statut.isBlank()) {
            return StatutModeration.EN_ATTENTE_VALIDATION;
        }
        String clean = statut.trim().toUpperCase();
        if ("EN_ATTENTE".equals(clean) || "EN_ATTENTE_VALIDATION".equals(clean)) {
            return StatutModeration.EN_ATTENTE_VALIDATION;
        }
        try {
            return StatutModeration.valueOf(clean);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Statut de modération inconnu : '" + statut + "'. Valeurs acceptées : EN_ATTENTE, EN_ATTENTE_VALIDATION, VALIDE, REJETE.");
        }
    }

    private String extraireMotif(String paramMotif, Map<String, String> body) {
        if (paramMotif != null && !paramMotif.isBlank()) {
            return paramMotif.trim();
        }
        if (body != null) {
            if (body.containsKey("motif") && body.get("motif") != null && !body.get("motif").isBlank()) {
                return body.get("motif").trim();
            }
            if (body.containsKey("motifRejet") && body.get("motifRejet") != null && !body.get("motifRejet").isBlank()) {
                return body.get("motifRejet").trim();
            }
        }
        return null;
    }
}
