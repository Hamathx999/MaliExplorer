package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.PresidentRequestDTO;
import com.maliexplorer_backend.dto.PresidentResponseDTO;
import com.maliexplorer_backend.service.PresidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presidents")
@RequiredArgsConstructor
@Tag(name = "Présidents", description = "Gestion des présidents et dirigeants historiques du Mali")
public class PresidentController {

    private final PresidentService presidentService;

    @GetMapping
    @Operation(summary = "Lister tous les présidents")
    public ResponseEntity<List<PresidentResponseDTO>> getAllPresidents() {
        return ResponseEntity.ok(presidentService.getAllPresidents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un président par ID")
    public ResponseEntity<PresidentResponseDTO> getPresidentById(@PathVariable Long id) {
        return ResponseEntity.ok(presidentService.getPresidentById(id));
    }

    @PostMapping
    @Operation(summary = "Ajouter un président")
    public ResponseEntity<PresidentResponseDTO> createPresident(@Valid @RequestBody PresidentRequestDTO requestDTO) {
        PresidentResponseDTO created = presidentService.createPresident(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les informations d'un président")
    public ResponseEntity<PresidentResponseDTO> updatePresident(
            @PathVariable Long id,
            @Valid @RequestBody PresidentRequestDTO requestDTO) {
        return ResponseEntity.ok(presidentService.updatePresident(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un président")
    public ResponseEntity<Void> deletePresident(@PathVariable Long id) {
        presidentService.deletePresident(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des présidents par nom ou prénom")
    public ResponseEntity<List<PresidentResponseDTO>> searchPresidents(@RequestParam String q) {
        return ResponseEntity.ok(presidentService.searchPresidents(q));
    }
}
