package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.TouristeRequestDTO;
import com.maliexplorer_backend.dto.TouristeResponseDTO;
import com.maliexplorer_backend.service.TouristeService;
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
@RequestMapping("/api/touristes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Touristes", description = "Gestion des touristes, profils, favoris et progression de découverte")
public class TouristeController {

    private final TouristeService touristeService;

    @GetMapping
    @Operation(summary = "Lister tous les touristes avec pagination (Admin uniquement)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TouristeResponseDTO>> obtenirTousLesTouristes(
            @PageableDefault(size = 10, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(touristeService.obtenirTousLesTouristes(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un touriste par son ID (Admin uniquement)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TouristeResponseDTO> obtenirTouristeParId(@PathVariable int id) {
        return ResponseEntity.ok(touristeService.obtenirTouristeParId(id));
    }

    @PostMapping
    @Operation(summary = "Créer un profil touriste (Admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TouristeResponseDTO> creerTouriste(@Valid @RequestBody TouristeRequestDTO requestDTO) {
        TouristeResponseDTO nouveau = touristeService.creerTouriste(requestDTO);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les données d'un touriste (Admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TouristeResponseDTO> mettreAJourTouriste(
            @PathVariable int id,
            @Valid @RequestBody TouristeRequestDTO requestDTO) {
        return ResponseEntity.ok(touristeService.mettreAJourTouriste(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un compte touriste (Admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerTouriste(@PathVariable int id) {
        touristeService.supprimerTouriste(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // Endpoints pour l'utilisateur connecté (Token Firebase requis)
    // ==========================================

    @GetMapping("/profil")
    @Operation(summary = "Consulter son propre profil touriste (Utilisateur connecté)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TouristeResponseDTO> getMonProfil() {
        return ResponseEntity.ok(touristeService.getProfilUtilisateurConnecte());
    }

    @PutMapping("/profil")
    @Operation(summary = "Mettre à jour son propre profil (Utilisateur connecté)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TouristeResponseDTO> mettreAJourMonProfil(@Valid @RequestBody TouristeRequestDTO requestDTO) {
        return ResponseEntity.ok(touristeService.mettreAJourProfil(requestDTO));
    }

    @PostMapping("/favoris/lieu/{idLieu}")
    @Operation(summary = "Marquer un lieu historique comme visité (+10 points)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TouristeResponseDTO> ajouterLieuVisite(@PathVariable Long idLieu) {
        return ResponseEntity.ok(touristeService.ajouterLieuVisite(idLieu));
    }

    @PostMapping("/favoris/article/{idArticle}")
    @Operation(summary = "Marquer un article culturel comme lu (+5 points)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TouristeResponseDTO> ajouterArticleLu(@PathVariable Long idArticle) {
        return ResponseEntity.ok(touristeService.ajouterArticleLu(idArticle));
    }
}
