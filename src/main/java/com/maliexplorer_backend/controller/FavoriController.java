package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.FavoriResponseDTO;
import com.maliexplorer_backend.service.FavoriService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoris")
@RequiredArgsConstructor
@Tag(name = "Favoris", description = "Gestion des favoris (Bookmarks) des utilisateurs (lieux historiques, articles)")
public class FavoriController {

    private final FavoriService favoriService;

    @PostMapping("/lieux/{lieuId}")
    @Operation(summary = "Ajouter un lieu historique aux favoris")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriResponseDTO> ajouterLieuFavori(@PathVariable Long lieuId) {
        FavoriResponseDTO response = favoriService.ajouterLieuFavori(lieuId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/lieux/{lieuId}")
    @Operation(summary = "Supprimer un lieu historique des favoris")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> supprimerLieuFavori(@PathVariable Long lieuId) {
        favoriService.supprimerLieuFavori(lieuId);
        return ResponseEntity.ok(Map.of("message", "Lieu historique retiré des favoris avec succès"));
    }

    @PostMapping("/articles/{articleId}")
    @Operation(summary = "Ajouter un article aux favoris")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriResponseDTO> ajouterArticleFavori(@PathVariable Long articleId) {
        FavoriResponseDTO response = favoriService.ajouterArticleFavori(articleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/articles/{articleId}")
    @Operation(summary = "Supprimer un article des favoris")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> supprimerArticleFavori(@PathVariable Long articleId) {
        favoriService.supprimerArticleFavori(articleId);
        return ResponseEntity.ok(Map.of("message", "Article retiré des favoris avec succès"));
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les favoris de l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriResponseDTO>> getMesFavoris() {
        return ResponseEntity.ok(favoriService.getMesFavoris());
    }

    @GetMapping("/lieux")
    @Operation(summary = "Obtenir tous les lieux favoris de l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriResponseDTO>> getMesFavorisLieux() {
        return ResponseEntity.ok(favoriService.getMesFavorisLieux());
    }

    @GetMapping("/articles")
    @Operation(summary = "Obtenir tous les articles favoris de l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriResponseDTO>> getMesFavorisArticles() {
        return ResponseEntity.ok(favoriService.getMesFavorisArticles());
    }

    @GetMapping("/lieux/{lieuId}/exists")
    @Operation(summary = "Vérifier si un lieu historique est dans les favoris")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> estLieuFavori(@PathVariable Long lieuId) {
        boolean exists = favoriService.estLieuFavori(lieuId);
        return ResponseEntity.ok(Map.of("isFavori", exists));
    }

    @GetMapping("/articles/{articleId}/exists")
    @Operation(summary = "Vérifier si un article est dans les favoris")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> estArticleFavori(@PathVariable Long articleId) {
        boolean exists = favoriService.estArticleFavori(articleId);
        return ResponseEntity.ok(Map.of("isFavori", exists));
    }
}
