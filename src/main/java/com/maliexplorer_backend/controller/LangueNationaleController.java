package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.LangueRequestDTO;
import com.maliexplorer_backend.dto.LangueResponseDTO;
import com.maliexplorer_backend.service.LangueNationaleService;
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
 * Contrôleur REST pour la consultation et la gestion des langues nationales du Mali.
 */
@RestController
@RequestMapping("/api/langues")
@RequiredArgsConstructor
@Tag(name = "Langues Nationales", description = "Endpoints de consultation et d'administration des 13 langues nationales du Mali")
public class LangueNationaleController {

    private final LangueNationaleService langueService;

    @GetMapping
    @Operation(summary = "Lister toutes les langues nationales publiées (Public)")
    public ResponseEntity<List<LangueResponseDTO>> getPublishedLangues() {
        return ResponseEntity.ok(langueService.getPublishedLangues());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister l'ensemble des langues nationales (Admin uniquement)")
    public ResponseEntity<List<LangueResponseDTO>> getAllLangues() {
        return ResponseEntity.ok(langueService.getAllLangues());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une langue nationale par ID (Public)")
    public ResponseEntity<LangueResponseDTO> getLangueById(@PathVariable Long id) {
        return ResponseEntity.ok(langueService.getLangueById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle langue nationale (Admin)")
    public ResponseEntity<LangueResponseDTO> createLangue(@Valid @RequestBody LangueRequestDTO requestDTO) {
        return new ResponseEntity<>(langueService.createLangue(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une langue nationale existante (Admin)")
    public ResponseEntity<LangueResponseDTO> updateLangue(
            @PathVariable Long id,
            @Valid @RequestBody LangueRequestDTO requestDTO) {
        return ResponseEntity.ok(langueService.updateLangue(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une langue nationale (Admin)")
    public ResponseEntity<Void> deleteLangue(@PathVariable Long id) {
        langueService.deleteLangue(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-publication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Basculer la publication d'une langue nationale (Admin)")
    public ResponseEntity<LangueResponseDTO> togglePublication(@PathVariable Long id) {
        return ResponseEntity.ok(langueService.togglePublication(id));
    }
}
