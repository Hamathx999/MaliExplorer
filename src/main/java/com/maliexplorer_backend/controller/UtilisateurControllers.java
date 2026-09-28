package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.Utilisateur;
import com.maliexplorer_backend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*") // Permet de tester facilement avec frontend (React/Flutter/Vue, etc.)
public class UtilisateurControllers {

    private final UtilisateurService service;

    public UtilisateurControllers(UtilisateurService service) {
        this.service = service;
    }

    /**
     * CREATE : Ajouter un nouvel utilisateur
     * POST /api/utilisateurs
     */
    @PostMapping
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        Utilisateur nouveau = service.creerUtilisateur(utilisateur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    /**
     * READ ALL : Récupérer tous les utilisateurs
     * GET /api/utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<Utilisateur>> obtenirTousLesUtilisateurs() {
        List<Utilisateur> liste = service.obtenirTousLesUtilisateurs();
        return ResponseEntity.ok(liste);
    }

    /**
     * READ ONE : Récupérer un utilisateur par son ID
     * GET /api/utilisateurs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateurParId(@PathVariable int id) {
        Utilisateur utilisateur = service.obtenirUtilisateurParId(id);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * UPDATE : Mettre à jour un utilisateur
     * PUT /api/utilisateurs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(
            @PathVariable int id,
            @Valid @RequestBody Utilisateur utilisateurModifie) {
        Utilisateur maj = service.mettreAJourUtilisateur(id, utilisateurModifie);
        return ResponseEntity.ok(maj);
    }

    /**
     * DELETE : Supprimer un utilisateur
     * DELETE /api/utilisateurs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable int id) {
        service.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
