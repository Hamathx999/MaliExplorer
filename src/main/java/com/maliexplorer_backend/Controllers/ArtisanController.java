package com.maliexplorer_backend.Controllers;

import com.maliexplorer_backend.model.Artisan;
import com.maliexplorer_backend.service.ArtisanService;
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
    public ResponseEntity<Artisan> creerArtisan(@Valid @RequestBody Artisan artisan) {
        Artisan nouveau = service.creerArtisan(artisan);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Artisan>> obtenirTousLesArtisans() {
        return ResponseEntity.ok(service.obtenirTousLesArtisans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artisan> obtenirArtisanParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirArtisanParId(id));
    }

    @GetMapping("/type/{typeArtisanat}")
    public ResponseEntity<List<Artisan>> rechercherParType(@PathVariable String typeArtisanat) {
        return ResponseEntity.ok(service.rechercherParType(typeArtisanat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artisan> mettreAJourArtisan(@PathVariable int id,
            @Valid @RequestBody Artisan artisan) {
        return ResponseEntity.ok(service.mettreAJourArtisan(id, artisan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerArtisan(@PathVariable int id) {
        service.supprimerArtisan(id);
        return ResponseEntity.noContent().build();
    }
}
