package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.AdministrateurRequestDTO;
import com.maliexplorer_backend.dto.AdministrateurResponseDTO;
import com.maliexplorer_backend.service.AdministrateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrateurResponseDTO> creerAdministrateur(
            @Valid @RequestBody AdministrateurRequestDTO administrateur) {
        AdministrateurResponseDTO nouveau = service.creerAdministrateur(administrateur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdministrateurResponseDTO>> obtenirTousLesAdministrateurs() {
        return ResponseEntity.ok(service.obtenirTousLesAdministrateurs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrateurResponseDTO> obtenirAdministrateurParId(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenirAdministrateurParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrateurResponseDTO> mettreAJourAdministrateur(@PathVariable int id,
            @Valid @RequestBody AdministrateurRequestDTO administrateur) {
        return ResponseEntity.ok(service.mettreAJourAdministrateur(id, administrateur));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerAdministrateur(@PathVariable int id) {
        service.supprimerAdministrateur(id);
        return ResponseEntity.noContent().build();
    }
}
