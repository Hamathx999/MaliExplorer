package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.service.OpportuniteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunites")
@RequiredArgsConstructor
@Tag(name = "Opportunités B2B", description = "Endpoints de consultation du catalogue d'opportunités de partenariats pour touristes et investisseurs B2B")
public class OpportuniteController {

    private final OpportuniteService opportuniteService;

    @GetMapping
    @Operation(summary = "Lister les opportunités validées", description = "Retourne la liste des artisans, guides et promoteurs ayant activé la recherche de partenariat et validés par l'administrateur. Filtrage optionnel par type (ARTISAN, PROMOTEUR, GUIDE).")
    public ResponseEntity<List<OpportuniteResponseDTO>> getOpportunites(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(opportuniteService.getOpportunitesValidees(type));
    }

    @GetMapping("/{idUsers}")
    @Operation(summary = "Consulter le détail d'une opportunité partenaire")
    public ResponseEntity<OpportuniteResponseDTO> getOpportuniteById(@PathVariable int idUsers) {
        return ResponseEntity.ok(opportuniteService.getMonStatutPartenaire(idUsers));
    }
}
