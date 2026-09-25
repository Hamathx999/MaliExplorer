package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.PlatRequestDTO;
import com.maliexplorer_backend.dto.PlatResponseDTO;
import com.maliexplorer_backend.service.PlatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
@RequiredArgsConstructor
@Tag(name = "Plats", description = "Gestion des plats traditionnels et gastronomie malienne")
public class PlatController {

    private final PlatService platService;

    @GetMapping
    @Operation(summary = "Lister tous les plats traditionnels")
    public ResponseEntity<List<PlatResponseDTO>> getAllPlats() {
        return ResponseEntity.ok(platService.getAllPlats());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un plat par ID")
    public ResponseEntity<PlatResponseDTO> getPlatById(@PathVariable Long id) {
        return ResponseEntity.ok(platService.getPlatById(id));
    }

    @GetMapping("/region/{regionId}")
    @Operation(summary = "Lister les plats associés à une région")
    public ResponseEntity<List<PlatResponseDTO>> getPlatsByRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(platService.getPlatsByRegion(regionId));
    }

    @GetMapping("/ethnie/{ethnieId}")
    @Operation(summary = "Lister les plats associés à une ethnie")
    public ResponseEntity<List<PlatResponseDTO>> getPlatsByEthnie(@PathVariable Long ethnieId) {
        return ResponseEntity.ok(platService.getPlatsByEthnie(ethnieId));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau plat")
    public ResponseEntity<PlatResponseDTO> createPlat(@Valid @RequestBody PlatRequestDTO requestDTO) {
        PlatResponseDTO created = platService.createPlat(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un plat existant")
    public ResponseEntity<PlatResponseDTO> updatePlat(
            @PathVariable Long id,
            @Valid @RequestBody PlatRequestDTO requestDTO) {
        return ResponseEntity.ok(platService.updatePlat(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un plat")
    public ResponseEntity<Void> deletePlat(@PathVariable Long id) {
        platService.deletePlat(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des plats par nom ou ingrédient")
    public ResponseEntity<List<PlatResponseDTO>> searchPlats(@RequestParam String q) {
        return ResponseEntity.ok(platService.searchPlats(q));
    }
}
