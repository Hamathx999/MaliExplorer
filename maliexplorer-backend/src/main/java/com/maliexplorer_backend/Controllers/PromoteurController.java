package com.maliexplorer_backend.Controllers;

import com.maliexplorer_backend.Models.PromoteurModel;
import com.maliexplorer_backend.Services.PromoteurService;
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
    public ResponseEntity<PromoteurModel> creerPromoteur(@Valid @RequestBody PromoteurModel promoteur) {
        PromoteurModel nouveau = service.creerPromoteur(promoteur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PromoteurModel>> obtenirTousLesPromoteurs() {
        return ResponseEntity.ok(service.obtenirTousLesPromoteurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromoteurModel> obtenirPromoteurParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirPromoteurParId(id));
    }

    @GetMapping("/organisation/{nomOrganisation}")
    public ResponseEntity<List<PromoteurModel>> rechercherParOrganisation(@PathVariable String nomOrganisation) {
        return ResponseEntity.ok(service.rechercherParOrganisation(nomOrganisation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromoteurModel> mettreAJourPromoteur(@PathVariable int id,
            @Valid @RequestBody PromoteurModel promoteur) {
        return ResponseEntity.ok(service.mettreAJourPromoteur(id, promoteur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPromoteur(@PathVariable int id) {
        service.supprimerPromoteur(id);
        return ResponseEntity.noContent().build();
    }
}
