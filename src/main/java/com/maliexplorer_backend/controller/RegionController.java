package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.RegionRequestDTO;
import com.maliexplorer_backend.dto.RegionResponseDTO;
import com.maliexplorer_backend.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@Tag(name = "Régions", description = "Gestion des régions du Mali")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "Lister toutes les régions")
    public ResponseEntity<List<RegionResponseDTO>> getAllRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une région par ID")
    public ResponseEntity<RegionResponseDTO> getRegionById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getRegionById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle région")
    public ResponseEntity<RegionResponseDTO> createRegion(@Valid @RequestBody RegionRequestDTO requestDTO) {
        RegionResponseDTO created = regionService.createRegion(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une région existante")
    public ResponseEntity<RegionResponseDTO> updateRegion(
            @PathVariable Long id,
            @Valid @RequestBody RegionRequestDTO requestDTO) {
        return ResponseEntity.ok(regionService.updateRegion(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une région")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des régions par mot-clé")
    public ResponseEntity<List<RegionResponseDTO>> searchRegions(@RequestParam String q) {
        return ResponseEntity.ok(regionService.searchRegions(q));
    }
}
