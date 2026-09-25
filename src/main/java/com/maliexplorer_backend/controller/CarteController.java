package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.MarqueurCarteDTO;
import com.maliexplorer_backend.service.CarteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carte")
@RequiredArgsConstructor
@Tag(name = "Carte Interactive", description = "Données géographiques et marqueurs pour la carte interactive du Mali")
public class CarteController {

    private final CarteService carteService;

    @GetMapping("/marqueurs")
    @Operation(summary = "Obtenir tous les marqueurs pour la carte interactive (ou filtrés par type)")
    public ResponseEntity<List<MarqueurCarteDTO>> getMarqueurs(
            @RequestParam(required = false) String type) {
        if (type != null && !type.trim().isEmpty()) {
            return ResponseEntity.ok(carteService.getMarqueursByType(type));
        }
        return ResponseEntity.ok(carteService.getAllMarqueurs());
    }

    @GetMapping("/panoramas-360")
    @Operation(summary = "Obtenir tous les marqueurs géolocalisés disposant d'un panorama 360°")
    public ResponseEntity<List<MarqueurCarteDTO>> getMarqueursWith360() {
        return ResponseEntity.ok(carteService.getMarqueursWith360());
    }
}
