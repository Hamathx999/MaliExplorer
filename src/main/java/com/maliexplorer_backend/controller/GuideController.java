package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.GuideRequestDTO;
import com.maliexplorer_backend.dto.GuideResponseDTO;
import com.maliexplorer_backend.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*")
public class GuideController {

    private final GuideService service;

    public GuideController(GuideService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuideResponseDTO> creerGuide(@Valid @RequestBody GuideRequestDTO guide) {
        GuideResponseDTO nouveau = service.creerGuide(guide);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GuideResponseDTO>> obtenirTousLesGuides() {
        return ResponseEntity.ok(service.obtenirTousLesGuides());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> obtenirGuideParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirGuideParId(id));
    }

    @GetMapping("/langue/{langue}")
    public ResponseEntity<List<GuideResponseDTO>> rechercherParLangue(@PathVariable String langue) {
        return ResponseEntity.ok(service.rechercherParLangue(langue));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuideResponseDTO> mettreAJourGuide(@PathVariable int id,
            @Valid @RequestBody GuideRequestDTO guide) {
        return ResponseEntity.ok(service.mettreAJourGuide(id, guide));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerGuide(@PathVariable int id) {
        service.supprimerGuide(id);
        return ResponseEntity.noContent().build();
    }
}
