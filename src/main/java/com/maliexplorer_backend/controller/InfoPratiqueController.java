package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.InfoPratiqueRequestDTO;
import com.maliexplorer_backend.dto.InfoPratiqueResponseDTO;
import com.maliexplorer_backend.service.InfoPratiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la consultation et la gestion des informations pratiques du voyageur au Mali.
 */
@RestController
@RequestMapping("/api/infos-pratiques")
@RequiredArgsConstructor
@Tag(name = "Informations Pratiques", description = "Endpoints de consultation et d'administration des fiches pratiques pour les voyageurs au Mali")
public class InfoPratiqueController {

    private final InfoPratiqueService infoPratiqueService;

    @GetMapping
    @Operation(summary = "Lister toutes les fiches d'informations pratiques publiées (Public)")
    public ResponseEntity<List<InfoPratiqueResponseDTO>> getPublishedInfosPratiques() {
        return ResponseEntity.ok(infoPratiqueService.getPublishedInfosPratiques());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister l'ensemble des fiches d'informations pratiques (Admin uniquement)")
    public ResponseEntity<List<InfoPratiqueResponseDTO>> getAllInfosPratiques() {
        return ResponseEntity.ok(infoPratiqueService.getAllInfosPratiques());
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Lister les fiches publiées par catégorie (Public)")
    public ResponseEntity<List<InfoPratiqueResponseDTO>> getInfosPratiquesByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(infoPratiqueService.getInfosPratiquesByCategorie(categorie));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une fiche pratique par ID (Public)")
    public ResponseEntity<InfoPratiqueResponseDTO> getInfoPratiqueById(@PathVariable Long id) {
        return ResponseEntity.ok(infoPratiqueService.getInfoPratiqueById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle fiche d'information pratique (Admin)")
    public ResponseEntity<InfoPratiqueResponseDTO> createInfoPratique(@Valid @RequestBody InfoPratiqueRequestDTO requestDTO) {
        return new ResponseEntity<>(infoPratiqueService.createInfoPratique(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une fiche d'information pratique existante (Admin)")
    public ResponseEntity<InfoPratiqueResponseDTO> updateInfoPratique(
            @PathVariable Long id,
            @Valid @RequestBody InfoPratiqueRequestDTO requestDTO) {
        return ResponseEntity.ok(infoPratiqueService.updateInfoPratique(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une fiche d'information pratique (Admin)")
    public ResponseEntity<Void> deleteInfoPratique(@PathVariable Long id) {
        infoPratiqueService.deleteInfoPratique(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-publication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Basculer la publication d'une fiche pratique (Admin)")
    public ResponseEntity<InfoPratiqueResponseDTO> togglePublication(@PathVariable Long id) {
        return ResponseEntity.ok(infoPratiqueService.togglePublication(id));
    }
}
