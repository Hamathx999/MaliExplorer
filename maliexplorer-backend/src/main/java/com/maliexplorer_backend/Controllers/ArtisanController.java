package com.maliexplorer_backend.Controllers;

import com.maliexplorer_backend.Models.ArtisanModel;
import com.maliexplorer_backend.Services.ArtisanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ArtisanModel> creerArtisan(@Valid @RequestBody ArtisanModel artisan) {
        ArtisanModel nouveau = service.creerArtisan(artisan);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ArtisanModel>> obtenirTousLesArtisans() {
        return ResponseEntity.ok(service.obtenirTousLesArtisans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtisanModel> obtenirArtisanParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirArtisanParId(id));
    }

    @GetMapping("/type/{typeArtisanat}")
    public ResponseEntity<List<ArtisanModel>> rechercherParType(@PathVariable String typeArtisanat) {
        return ResponseEntity.ok(service.rechercherParType(typeArtisanat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtisanModel> mettreAJourArtisan(@PathVariable int id,
            @Valid @RequestBody ArtisanModel artisan) {
        return ResponseEntity.ok(service.mettreAJourArtisan(id, artisan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerArtisan(@PathVariable int id) {
        service.supprimerArtisan(id);
        return ResponseEntity.noContent().build();
    }
}
