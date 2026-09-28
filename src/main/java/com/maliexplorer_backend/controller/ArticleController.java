package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.ArticleRequestDTO;
import com.maliexplorer_backend.dto.ArticleResponseDTO;
import com.maliexplorer_backend.dto.ArticleSummaryDTO;
import com.maliexplorer_backend.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Gestion des articles culturels et touristiques avec pagination")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "Lister les articles avec pagination et tri")
    public ResponseEntity<Page<ArticleResponseDTO>> getAllArticles(
            @PageableDefault(size = 10, sort = "datePublication", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un article par ID (incrémente les vues)")
    public ResponseEntity<ArticleResponseDTO> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/recents")
    @Operation(summary = "Obtenir les articles les plus récents (Top 5)")
    public ResponseEntity<List<ArticleSummaryDTO>> getRecentArticles() {
        return ResponseEntity.ok(articleService.getRecentArticles());
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Lister les articles filtrés par catégorie avec pagination")
    public ResponseEntity<Page<ArticleResponseDTO>> getArticlesByCategorie(
            @PathVariable String categorie,
            @PageableDefault(size = 10, sort = "datePublication", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(articleService.getArticlesByCategorie(categorie, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des articles par mot-clé (titre ou contenu) avec pagination")
    public ResponseEntity<Page<ArticleResponseDTO>> searchArticles(
            @RequestParam String q,
            @PageableDefault(size = 10, sort = "datePublication", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(articleService.searchArticles(q, pageable));
    }

    @PostMapping
    @Operation(summary = "Publier un nouvel article")
    public ResponseEntity<ArticleResponseDTO> createArticle(@Valid @RequestBody ArticleRequestDTO requestDTO) {
        ArticleResponseDTO created = articleService.createArticle(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un article existant")
    public ResponseEntity<ArticleResponseDTO> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequestDTO requestDTO) {
        return ResponseEntity.ok(articleService.updateArticle(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un article")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
