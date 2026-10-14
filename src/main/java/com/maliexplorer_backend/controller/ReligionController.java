package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.ReligionRequestDTO;
import com.maliexplorer_backend.dto.ReligionResponseDTO;
import com.maliexplorer_backend.service.ReligionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/religions")
@RequiredArgsConstructor
@Tag(name = "Religions au Mali", description = "Gestion encyclopédique et statistique des religions et traditions religieuses au Mali")
public class ReligionController {

    private final ReligionService religionService;

    @GetMapping
    @Operation(summary = "Lister toutes les religions publiées (Public)")
    public ResponseEntity<List<ReligionResponseDTO>> getPublishedReligions() {
        return ResponseEntity.ok(religionService.getPublishedReligions());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les fiches de religions (Admin uniquement)")
    public ResponseEntity<List<ReligionResponseDTO>> getAllReligions() {
        return ResponseEntity.ok(religionService.getAllReligions());
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher parmi les religions et leurs branches (Public)")
    public ResponseEntity<List<ReligionResponseDTO>> searchReligions(@RequestParam String q) {
        return ResponseEntity.ok(religionService.searchReligions(q));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une religion par ID (Public)")
    public ResponseEntity<ReligionResponseDTO> getReligionById(@PathVariable Long id) {
        return ResponseEntity.ok(religionService.getReligionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une fiche de religion (Admin)")
    public ResponseEntity<ReligionResponseDTO> createReligion(@Valid @RequestBody ReligionRequestDTO requestDTO) {
        return new ResponseEntity<>(religionService.createReligion(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une fiche de religion existante (Admin)")
    public ResponseEntity<ReligionResponseDTO> updateReligion(
            @PathVariable Long id,
            @Valid @RequestBody ReligionRequestDTO requestDTO) {
        return ResponseEntity.ok(religionService.updateReligion(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une fiche de religion (Admin)")
    public ResponseEntity<Void> deleteReligion(@PathVariable Long id) {
        religionService.deleteReligion(id);
        return ResponseEntity.noContent().build();
    }
}

