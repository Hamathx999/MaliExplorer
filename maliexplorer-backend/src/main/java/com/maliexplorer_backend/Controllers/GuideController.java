package com.maliexplorer_backend.Controllers;

import com.maliexplorer_backend.Models.GuideModel;
import com.maliexplorer_backend.Services.GuideService;
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
    public ResponseEntity<GuideModel> creerGuide(@Valid @RequestBody GuideModel guide) {
        GuideModel nouveau = service.creerGuide(guide);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GuideModel>> obtenirTousLesGuides() {
        return ResponseEntity.ok(service.obtenirTousLesGuides());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideModel> obtenirGuideParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirGuideParId(id));
    }

    @GetMapping("/langue/{langue}")
    public ResponseEntity<List<GuideModel>> rechercherParLangue(@PathVariable String langue) {
        return ResponseEntity.ok(service.rechercherParLangue(langue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideModel> mettreAJourGuide(@PathVariable int id, @Valid @RequestBody GuideModel guide) {
        return ResponseEntity.ok(service.mettreAJourGuide(id, guide));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerGuide(@PathVariable int id) {
        service.supprimerGuide(id);
        return ResponseEntity.noContent().build();
    }
}
