package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.SearchResultItemDTO;
import com.maliexplorer_backend.service.RechercheGlobaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recherche")
@RequiredArgsConstructor
@Tag(name = "Recherche Globale", description = "Endpoint de recherche transversale (villes, régions, sites, plats, ethnies, religions, présidents, guides)")
public class RechercheGlobaleController {

    private final RechercheGlobaleService rechercheGlobaleService;

    @GetMapping
    @Operation(summary = "Rechercher tous types de contenus avec autocomplétion")
    public ResponseEntity<List<SearchResultItemDTO>> rechercher(@RequestParam(name = "q", defaultValue = "") String q) {
        return ResponseEntity.ok(rechercheGlobaleService.rechercher(q));
    }
}

