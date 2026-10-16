package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.HomeSlideRequestDTO;
import com.maliexplorer_backend.dto.HomeSlideResponseDTO;
import com.maliexplorer_backend.service.HomeSlideService;
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
@RequestMapping("/api/home-slides")
@RequiredArgsConstructor
@Tag(name = "Diaporama Home", description = "Gestion du diaporama de photos et bannières de l'écran d'accueil")
public class HomeSlideController {

    private final HomeSlideService homeSlideService;

    @GetMapping({"", "/actives"})
    @Operation(summary = "Lister les diapositives actives (Public)")
    public ResponseEntity<List<HomeSlideResponseDTO>> getActiveSlides() {
        return ResponseEntity.ok(homeSlideService.getActiveSlides());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les diapositives (Admin uniquement)")
    public ResponseEntity<List<HomeSlideResponseDTO>> getAllSlides() {
        return ResponseEntity.ok(homeSlideService.getAllSlides());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une diapositive par ID")
    public ResponseEntity<HomeSlideResponseDTO> getSlideById(@PathVariable Long id) {
        return ResponseEntity.ok(homeSlideService.getSlideById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle diapositive (Admin)")
    public ResponseEntity<HomeSlideResponseDTO> createSlide(@Valid @RequestBody HomeSlideRequestDTO requestDTO) {
        return new ResponseEntity<>(homeSlideService.createSlide(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une diapositive existante (Admin)")
    public ResponseEntity<HomeSlideResponseDTO> updateSlide(
            @PathVariable Long id,
            @Valid @RequestBody HomeSlideRequestDTO requestDTO) {
        return ResponseEntity.ok(homeSlideService.updateSlide(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une diapositive (Admin)")
    public ResponseEntity<Void> deleteSlide(@PathVariable Long id) {
        homeSlideService.deleteSlide(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-actif")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer ou désactiver une diapositive (Admin)")
    public ResponseEntity<HomeSlideResponseDTO> toggleActif(@PathVariable Long id) {
        HomeSlideResponseDTO slide = homeSlideService.getSlideById(id);
        HomeSlideRequestDTO req = HomeSlideRequestDTO.builder()
                .titre(slide.getTitre())
                .sousTitre(slide.getSousTitre())
                .legende(slide.getLegende())
                .imageUrl(slide.getImageUrl())
                .lienAction(slide.getLienAction())
                .ordre(slide.getOrdre())
                .actif(slide.getActif() == null || !slide.getActif())
                .prioritaire(slide.getPrioritaire())
                .build();
        return ResponseEntity.ok(homeSlideService.updateSlide(id, req));
    }

    @PatchMapping("/{id}/set-principal")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Définir une diapositive comme prioritaire (Admin)")
    public ResponseEntity<HomeSlideResponseDTO> setPrincipal(@PathVariable Long id) {
        HomeSlideResponseDTO slide = homeSlideService.getSlideById(id);
        HomeSlideRequestDTO req = HomeSlideRequestDTO.builder()
                .titre(slide.getTitre())
                .sousTitre(slide.getSousTitre())
                .legende(slide.getLegende())
                .imageUrl(slide.getImageUrl())
                .lienAction(slide.getLienAction())
                .ordre(slide.getOrdre())
                .actif(slide.getActif())
                .prioritaire(true)
                .build();
        return ResponseEntity.ok(homeSlideService.updateSlide(id, req));
    }

    @RequestMapping(value = {"/reorder", "/reordonner"}, method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Réordonner les diapositives (Admin)")
    public ResponseEntity<List<HomeSlideResponseDTO>> reorderSlides(@RequestBody List<Long> slideIds) {
        homeSlideService.reorderSlides(slideIds);
        return ResponseEntity.ok(homeSlideService.getAllSlides());
    }
}

