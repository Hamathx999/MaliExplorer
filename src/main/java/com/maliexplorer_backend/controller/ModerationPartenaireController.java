package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.service.OpportuniteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/moderation/opportunites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Modération Admin Partenariats", description = "Validation ou rejet des projets et demandes de partenariat déposés par les partenaires")
@SecurityRequirement(name = "Bearer Authentication")
public class ModerationPartenaireController {

    private final OpportuniteService opportuniteService;

    @GetMapping
    @Operation(summary = "Lister les fiches partenaires en attente de modération")
    public ResponseEntity<List<OpportuniteResponseDTO>> getEnAttente() {
        return ResponseEntity.ok(opportuniteService.getOpportunitesEnAttente());
    }

    @PatchMapping("/{idUsers}/valider")
    @Operation(summary = "Valider une fiche partenaire (Statut -> VALIDE)")
    public ResponseEntity<OpportuniteResponseDTO> valider(@PathVariable int idUsers) {
        return ResponseEntity.ok(opportuniteService.validerOpportunite(idUsers));
    }

    @PatchMapping("/{idUsers}/rejeter")
    @Operation(summary = "Rejeter une fiche partenaire (Statut -> REJETE) avec motif optionnel")
    public ResponseEntity<OpportuniteResponseDTO> rejeter(
            @PathVariable int idUsers,
            @RequestParam(required = false) String motif) {
        return ResponseEntity.ok(opportuniteService.rejeterOpportunite(idUsers, motif));
    }
}
