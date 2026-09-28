package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.Promoteur;
import com.maliexplorer_backend.service.PromoteurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promoteurs")
@CrossOrigin(origins = "*")
public class PromoteurController {

    private final PromoteurService service;

    public PromoteurController(PromoteurService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Promoteur> creerPromoteur(@Valid @RequestBody Promoteur promoteur) {
        Promoteur nouveau = service.creerPromoteur(promoteur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Promoteur>> obtenirTousLesPromoteurs() {
        return ResponseEntity.ok(service.obtenirTousLesPromoteurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promoteur> obtenirPromoteurParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirPromoteurParId(id));
    }

    @GetMapping("/organisation/{nomOrganisation}")
    public ResponseEntity<List<Promoteur>> rechercherParOrganisation(@PathVariable String nomOrganisation) {
        return ResponseEntity.ok(service.rechercherParOrganisation(nomOrganisation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promoteur> mettreAJourPromoteur(@PathVariable int id,
            @Valid @RequestBody Promoteur promoteur) {
        return ResponseEntity.ok(service.mettreAJourPromoteur(id, promoteur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPromoteur(@PathVariable int id) {
        service.supprimerPromoteur(id);
        return ResponseEntity.noContent().build();
    }
}
