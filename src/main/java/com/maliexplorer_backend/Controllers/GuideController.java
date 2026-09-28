package com.maliexplorer_backend.Controllers;

import com.maliexplorer_backend.model.Guide;
import com.maliexplorer_backend.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Guide> creerGuide(@Valid @RequestBody Guide guide) {
        Guide nouveau = service.creerGuide(guide);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Guide>> obtenirTousLesGuides() {
        return ResponseEntity.ok(service.obtenirTousLesGuides());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guide> obtenirGuideParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirGuideParId(id));
    }

    @GetMapping("/langue/{langue}")
    public ResponseEntity<List<Guide>> rechercherParLangue(@PathVariable String langue) {
        return ResponseEntity.ok(service.rechercherParLangue(langue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guide> mettreAJourGuide(@PathVariable int id, @Valid @RequestBody Guide guide) {
        return ResponseEntity.ok(service.mettreAJourGuide(id, guide));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerGuide(@PathVariable int id) {
        service.supprimerGuide(id);
        return ResponseEntity.noContent().build();
    }
}
