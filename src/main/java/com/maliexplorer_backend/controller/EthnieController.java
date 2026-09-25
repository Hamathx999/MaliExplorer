package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.EthnieRequestDTO;
import com.maliexplorer_backend.dto.EthnieResponseDTO;
import com.maliexplorer_backend.service.EthnieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ethnies")
@RequiredArgsConstructor
@Tag(name = "Ethnies", description = "Gestion des ethnies et de la diversité culturelle malienne")
public class EthnieController {

    private final EthnieService ethnieService;

    @GetMapping
    @Operation(summary = "Lister toutes les ethnies")
    public ResponseEntity<List<EthnieResponseDTO>> getAllEthnies() {
        return ResponseEntity.ok(ethnieService.getAllEthnies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une ethnie par ID")
    public ResponseEntity<EthnieResponseDTO> getEthnieById(@PathVariable Long id) {
        return ResponseEntity.ok(ethnieService.getEthnieById(id));
    }

    @GetMapping("/region/{regionId}")
    @Operation(summary = "Lister les ethnies présentes dans une région")
    public ResponseEntity<List<EthnieResponseDTO>> getEthniesByRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(ethnieService.getEthniesByRegion(regionId));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle ethnie")
    public ResponseEntity<EthnieResponseDTO> createEthnie(@Valid @RequestBody EthnieRequestDTO requestDTO) {
        EthnieResponseDTO created = ethnieService.createEthnie(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ethnie existante")
    public ResponseEntity<EthnieResponseDTO> updateEthnie(
            @PathVariable Long id,
            @Valid @RequestBody EthnieRequestDTO requestDTO) {
        return ResponseEntity.ok(ethnieService.updateEthnie(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une ethnie")
    public ResponseEntity<Void> deleteEthnie(@PathVariable Long id) {
        ethnieService.deleteEthnie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des ethnies par mot-clé")
    public ResponseEntity<List<EthnieResponseDTO>> searchEthnies(@RequestParam String q) {
        return ResponseEntity.ok(ethnieService.searchEthnies(q));
    }
}
