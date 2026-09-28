package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.LieuHistoriqueRequestDTO;
import com.maliexplorer_backend.dto.LieuHistoriqueResponseDTO;
import com.maliexplorer_backend.service.LieuHistoriqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lieux-historiques")
@RequiredArgsConstructor
@Tag(name = "Lieux Historiques", description = "Gestion des monuments, sites et lieux historiques du Mali")
public class LieuHistoriqueController {

    private final LieuHistoriqueService lieuHistoriqueService;

    @GetMapping
    @Operation(summary = "Lister tous les lieux historiques")
    public ResponseEntity<List<LieuHistoriqueResponseDTO>> getAllLieux() {
        return ResponseEntity.ok(lieuHistoriqueService.getAllLieux());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un lieu historique par ID")
    public ResponseEntity<LieuHistoriqueResponseDTO> getLieuById(@PathVariable Long id) {
        return ResponseEntity.ok(lieuHistoriqueService.getLieuById(id));
    }

    @GetMapping("/ville/{villeId}")
    @Operation(summary = "Lister les lieux historiques situés dans une ville")
    public ResponseEntity<List<LieuHistoriqueResponseDTO>> getLieuxByVille(@PathVariable Long villeId) {
        return ResponseEntity.ok(lieuHistoriqueService.getLieuxByVille(villeId));
    }

    @GetMapping("/panoramas-360")
    @Operation(summary = "Lister les lieux historiques disposant d'un panorama 360°")
    public ResponseEntity<List<LieuHistoriqueResponseDTO>> getLieuxWithPanorama360() {
        return ResponseEntity.ok(lieuHistoriqueService.getLieuxWithPanorama360());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau lieu historique (avec coordonnées GPS et image/360)")
    public ResponseEntity<LieuHistoriqueResponseDTO> createLieu(@Valid @RequestBody LieuHistoriqueRequestDTO requestDTO) {
        LieuHistoriqueResponseDTO created = lieuHistoriqueService.createLieu(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un lieu historique")
    public ResponseEntity<LieuHistoriqueResponseDTO> updateLieu(
            @PathVariable Long id,
            @Valid @RequestBody LieuHistoriqueRequestDTO requestDTO) {
        return ResponseEntity.ok(lieuHistoriqueService.updateLieu(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un lieu historique")
    public ResponseEntity<Void> deleteLieu(@PathVariable Long id) {
        lieuHistoriqueService.deleteLieu(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des lieux historiques par nom")
    public ResponseEntity<List<LieuHistoriqueResponseDTO>> searchLieux(@RequestParam String q) {
        return ResponseEntity.ok(lieuHistoriqueService.searchLieux(q));
    }
}
