package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.ArtisanRequestDTO;
import com.maliexplorer_backend.dto.ArtisanResponseDTO;
import com.maliexplorer_backend.service.ArtisanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artisans")
@CrossOrigin(origins = "*")
public class ArtisanController {

    private final ArtisanService service;

    public ArtisanController(ArtisanService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ARTISAN') or hasRole('ADMIN')")
    public ResponseEntity<ArtisanResponseDTO> creerArtisan(@Valid @RequestBody ArtisanRequestDTO artisan) {
        ArtisanResponseDTO nouveau = service.creerArtisan(artisan);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ArtisanResponseDTO>> obtenirTousLesArtisans() {
        return ResponseEntity.ok(service.obtenirTousLesArtisans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtisanResponseDTO> obtenirArtisanParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirArtisanParId(id));
    }

    @GetMapping("/type/{typeArtisanat}")
    public ResponseEntity<List<ArtisanResponseDTO>> rechercherParType(@PathVariable String typeArtisanat) {
        return ResponseEntity.ok(service.rechercherParType(typeArtisanat));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ARTISAN') or hasRole('ADMIN')")
    public ResponseEntity<ArtisanResponseDTO> mettreAJourArtisan(@PathVariable int id,
            @Valid @RequestBody ArtisanRequestDTO artisan) {
        return ResponseEntity.ok(service.mettreAJourArtisan(id, artisan));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerArtisan(@PathVariable int id) {
        service.supprimerArtisan(id);
        return ResponseEntity.noContent().build();
    }
}
