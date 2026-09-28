package com.maliexplorer_backend.Controllers;


import com.maliexplorer_backend.model.Administrateur;
import com.maliexplorer_backend.service.AdministrateurService;
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
    public ResponseEntity<Administrateur> creerAdministrateur(
            @Valid @RequestBody Administrateur administrateur) {
        Administrateur nouveau = service.creerAdministrateur(administrateur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Administrateur>> obtenirTousLesAdministrateurs() {
        return ResponseEntity.ok(service.obtenirTousLesAdministrateurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrateur> obtenirAdministrateurParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirAdministrateurParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrateur> mettreAJourAdministrateur(@PathVariable int id,
            @Valid @RequestBody Administrateur administrateur) {
        return ResponseEntity.ok(service.mettreAJourAdministrateur(id, administrateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAdministrateur(@PathVariable int id) {
        service.supprimerAdministrateur(id);
        return ResponseEntity.noContent().build();
    }
}
