package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.TouristeRequestDTO;
import com.maliexplorer_backend.dto.TouristeResponseDTO;
import com.maliexplorer_backend.service.TouristeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TouristeResponseDTO> creerTouriste(@Valid @RequestBody TouristeRequestDTO touriste) {
        TouristeResponseDTO nouveau = service.creerTouriste(touriste);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TouristeResponseDTO>> obtenirTousLesTouristes() {
        return ResponseEntity.ok(service.obtenirTousLesTouristes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TouristeResponseDTO> obtenirTouristeParId(@PathVariable int id) {
        verifierAccesCompte(id, "consulter");
        return ResponseEntity.ok(service.obtenirTouristeParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TouristeResponseDTO> mettreAJourTouriste(@PathVariable int id,
            @Valid @RequestBody TouristeRequestDTO touriste) {
        verifierAccesCompte(id, "modifier");
        return ResponseEntity.ok(service.mettreAJourTouriste(id, touriste));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> supprimerTouriste(@PathVariable int id) {
        verifierAccesCompte(id, "supprimer");
        service.supprimerTouriste(id);
        return ResponseEntity.noContent().build();
    }

    private void verifierAccesCompte(int id, String action) {
        if (!SecurityUtils.isCurrentUserOrAdmin(id)) {
            throw new AccessDeniedException("Accès refusé : vous ne pouvez " + action + " que votre propre profil.");
        }
    }
}
