package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.PromoteurRequestDTO;
import com.maliexplorer_backend.dto.PromoteurResponseDTO;
import com.maliexplorer_backend.service.PromoteurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promoteurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Promoteurs", description = "Gestion des promoteurs culturels, partenaires et agences")
public class PromoteurController {

    private final PromoteurService promoteurService;

    @GetMapping
    @Operation(summary = "Lister tous les promoteurs avec pagination")
    public ResponseEntity<Page<PromoteurResponseDTO>> obtenirTousLesPromoteurs(
            @PageableDefault(size = 10, sort = "nomOrganisation") Pageable pageable) {
        return ResponseEntity.ok(promoteurService.obtenirTousLesPromoteurs(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un promoteur par son ID")
    public ResponseEntity<PromoteurResponseDTO> obtenirPromoteurParId(@PathVariable int id) {
        return ResponseEntity.ok(promoteurService.obtenirPromoteurParId(id));
    }

    @GetMapping("/organisation/{nomOrganisation}")
    @Operation(summary = "Rechercher des promoteurs par nom d'organisation avec pagination")
    public ResponseEntity<Page<PromoteurResponseDTO>> rechercherParOrganisation(
            @PathVariable String nomOrganisation,
            @PageableDefault(size = 10, sort = "nomOrganisation") Pageable pageable) {
        return ResponseEntity.ok(promoteurService.rechercherParOrganisation(nomOrganisation, pageable));
    }

    @PostMapping
    @Operation(summary = "Créer un profil promoteur (Admin uniquement)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoteurResponseDTO> creerPromoteur(@Valid @RequestBody PromoteurRequestDTO requestDTO) {
        PromoteurResponseDTO nouveau = promoteurService.creerPromoteur(requestDTO);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les données d'un promoteur (Admin uniquement)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoteurResponseDTO> mettreAJourPromoteur(
            @PathVariable int id,
            @Valid @RequestBody PromoteurRequestDTO requestDTO) {
        return ResponseEntity.ok(promoteurService.mettreAJourPromoteur(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un promoteur (Admin uniquement)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerPromoteur(@PathVariable int id) {
        promoteurService.supprimerPromoteur(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // Endpoints pour le promoteur connecté
    // ==========================================

    @GetMapping("/profil")
    @Operation(summary = "Consulter son propre profil promoteur (Utilisateur connecté)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PromoteurResponseDTO> getMonProfil() {
        return ResponseEntity.ok(promoteurService.getProfilUtilisateurConnecte());
    }

    @PutMapping("/profil")
    @Operation(summary = "Mettre à jour son propre profil promoteur (Utilisateur connecté)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PromoteurResponseDTO> mettreAJourMonProfil(@Valid @RequestBody PromoteurRequestDTO requestDTO) {
        return ResponseEntity.ok(promoteurService.mettreAJourProfil(requestDTO));
    }
}
