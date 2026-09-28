package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.TouristeModel;
import com.maliexplorer_backend.service.TouristeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/touristes")
@CrossOrigin(origins = "*")
public class TouristeController {

    private final TouristeService service;

    public TouristeController(TouristeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TouristeModel> creerTouriste(@Valid @RequestBody TouristeModel touriste) {
        TouristeModel nouveau = service.creerTouriste(touriste);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TouristeModel>> obtenirTousLesTouristes() {
        return ResponseEntity.ok(service.obtenirTousLesTouristes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TouristeModel> obtenirTouristeParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirTouristeParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TouristeModel> mettreAJourTouriste(@PathVariable int id,
            @Valid @RequestBody TouristeModel touriste) {
        return ResponseEntity.ok(service.mettreAJourTouriste(id, touriste));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTouriste(@PathVariable int id) {
        service.supprimerTouriste(id);
        return ResponseEntity.noContent().build();
    }
}
