package com.maliexplorer_backend.Controllers;


import com.maliexplorer_backend.Models.AdministrateurModel;
import com.maliexplorer_backend.Services.AdministrateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrateurs")
@CrossOrigin(origins = "*")
public class AdministrateurController {

    private final AdministrateurService service;

    public AdministrateurController(AdministrateurService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AdministrateurModel> creerAdministrateur(
            @Valid @RequestBody AdministrateurModel administrateur) {
        AdministrateurModel nouveau = service.creerAdministrateur(administrateur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AdministrateurModel>> obtenirTousLesAdministrateurs() {
        return ResponseEntity.ok(service.obtenirTousLesAdministrateurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrateurModel> obtenirAdministrateurParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirAdministrateurParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministrateurModel> mettreAJourAdministrateur(@PathVariable int id,
            @Valid @RequestBody AdministrateurModel administrateur) {
        return ResponseEntity.ok(service.mettreAJourAdministrateur(id, administrateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAdministrateur(@PathVariable int id) {
        service.supprimerAdministrateur(id);
        return ResponseEntity.noContent().build();
    }
}
