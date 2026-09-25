package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.PointInteretRequestDTO;
import com.maliexplorer_backend.dto.PointInteretResponseDTO;
import com.maliexplorer_backend.service.PointInteretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points-interet")
@RequiredArgsConstructor
@Tag(name = "Points d'Intérêt", description = "Gestion des points d'intérêt et repères touristiques")
public class PointInteretController {

    private final PointInteretService pointInteretService;

    @GetMapping
    @Operation(summary = "Lister tous les points d'intérêt")
    public ResponseEntity<List<PointInteretResponseDTO>> getAllPoints() {
        return ResponseEntity.ok(pointInteretService.getAllPoints());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un point d'intérêt par ID")
    public ResponseEntity<PointInteretResponseDTO> getPointById(@PathVariable Long id) {
        return ResponseEntity.ok(pointInteretService.getPointById(id));
    }

    @GetMapping("/region/{regionId}")
    @Operation(summary = "Lister les points d'intérêt d'une région")
    public ResponseEntity<List<PointInteretResponseDTO>> getPointsByRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(pointInteretService.getPointsByRegion(regionId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Lister les points d'intérêt par type")
    public ResponseEntity<List<PointInteretResponseDTO>> getPointsByType(@PathVariable String type) {
        return ResponseEntity.ok(pointInteretService.getPointsByType(type));
    }

    @PostMapping
    @Operation(summary = "Créer un point d'intérêt géolocalisé")
    public ResponseEntity<PointInteretResponseDTO> createPoint(@Valid @RequestBody PointInteretRequestDTO requestDTO) {
        PointInteretResponseDTO created = pointInteretService.createPoint(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un point d'intérêt")
    public ResponseEntity<PointInteretResponseDTO> updatePoint(
            @PathVariable Long id,
            @Valid @RequestBody PointInteretRequestDTO requestDTO) {
        return ResponseEntity.ok(pointInteretService.updatePoint(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un point d'intérêt")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        pointInteretService.deletePoint(id);
        return ResponseEntity.noContent().build();
    }
}
