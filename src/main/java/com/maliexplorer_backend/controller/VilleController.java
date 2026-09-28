package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.VilleRequestDTO;
import com.maliexplorer_backend.dto.VilleResponseDTO;
import com.maliexplorer_backend.service.VilleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villes")
@RequiredArgsConstructor
@Tag(name = "Villes", description = "Gestion des villes et communes du Mali")
public class VilleController {

    private final VilleService villeService;

    @GetMapping
    @Operation(summary = "Lister toutes les villes")
    public ResponseEntity<List<VilleResponseDTO>> getAllVilles() {
        return ResponseEntity.ok(villeService.getAllVilles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une ville par ID")
    public ResponseEntity<VilleResponseDTO> getVilleById(@PathVariable Long id) {
        return ResponseEntity.ok(villeService.getVilleById(id));
    }

    @GetMapping("/region/{regionId}")
    @Operation(summary = "Lister les villes rattachées à une région")
    public ResponseEntity<List<VilleResponseDTO>> getVillesByRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(villeService.getVillesByRegion(regionId));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle ville")
    public ResponseEntity<VilleResponseDTO> createVille(@Valid @RequestBody VilleRequestDTO requestDTO) {
        VilleResponseDTO created = villeService.createVille(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ville existante")
    public ResponseEntity<VilleResponseDTO> updateVille(
            @PathVariable Long id,
            @Valid @RequestBody VilleRequestDTO requestDTO) {
        return ResponseEntity.ok(villeService.updateVille(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une ville")
    public ResponseEntity<Void> deleteVille(@PathVariable Long id) {
        villeService.deleteVille(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des villes par mot-clé")
    public ResponseEntity<List<VilleResponseDTO>> searchVilles(@RequestParam String q) {
        return ResponseEntity.ok(villeService.searchVilles(q));
    }
}
