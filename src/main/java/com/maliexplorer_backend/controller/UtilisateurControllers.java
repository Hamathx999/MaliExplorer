package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurControllers {

    private final UtilisateurService service;
    private final utilisateurRepository userRepository;

    public UtilisateurControllers(UtilisateurService service, utilisateurRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    /**
     * Récupérer son propre profil connecté
     * GET /api/utilisateurs/me
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<utilisateurModel> obtenirMonProfil() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * UPDATE ME PHOTO : Mettre à jour sa propre photo de profil
     * PUT /api/utilisateurs/me/photo
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/photo")
    public ResponseEntity<utilisateurModel> mettreAJourMaPhoto(@RequestBody Map<String, String> payload) {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String photoUrl = payload.get("photoUrl");
        if (photoUrl == null || photoUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setPhotoUrl(photoUrl);
                    utilisateurModel saved = userRepository.save(user);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * UPDATE ME INFOS : Mettre à jour ses propres informations personnelles
     * PUT /api/utilisateurs/me
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<utilisateurModel> mettreAJourMesInfos(@RequestBody Map<String, String> payload) {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (payload.containsKey("prenom")) user.setPrenom(payload.get("prenom"));
                    if (payload.containsKey("nom")) user.setNom(payload.get("nom"));
                    if (payload.containsKey("adresse")) user.setAdresse(payload.get("adresse"));
                    if (payload.containsKey("telephone")) user.setTelephone(payload.get("telephone"));
                    if (payload.containsKey("photoUrl") && payload.get("photoUrl") != null && !payload.get("photoUrl").isBlank()) {
                        user.setPhotoUrl(payload.get("photoUrl"));
                    }
                    utilisateurModel saved = userRepository.save(user);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * TOGGLE STATUS : Bloquer ou débloquer un utilisateur (Admin)
     * Répercute immédiatement l'état sur Firebase Cloud si l'utilisateur possède un compte Firebase.
     * PATCH /api/utilisateurs/{id}/toggle-status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<utilisateurModel> toggleStatutUtilisateur(@PathVariable int id) {
        return userRepository.findById(id).map(user -> {
            String current = user.getStatut();
            boolean bloquer = !"BLOQUE".equalsIgnoreCase(current);
            user.setStatut(bloquer ? "BLOQUE" : "ACTIF");
            utilisateurModel saved = userRepository.save(user);

            // Synchronisation avec Firebase Cloud en arrière-plan (asynchrone, ne bloque pas la réponse HTTP)
            String uid = saved.getFirebaseUid();
            if (uid != null && !uid.isBlank() && !com.google.firebase.FirebaseApp.getApps().isEmpty()) {
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        com.google.firebase.auth.UserRecord.UpdateRequest updateRequest =
                                new com.google.firebase.auth.UserRecord.UpdateRequest(uid)
                                        .setDisabled(bloquer);
                        com.google.firebase.auth.FirebaseAuth.getInstance().updateUser(updateRequest);
                        log.info("Statut Firebase synchronisé en arrière-plan pour UID={}: disabled={}", uid, bloquer);
                    } catch (Exception ex) {
                        log.warn("Notice: Impossible de mettre à jour le statut Firebase Cloud pour UID={}: {}", uid, ex.getMessage());
                    }
                });
            }

            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * CREATE : Ajouter un nouvel utilisateur
     * POST /api/utilisateurs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<utilisateurModel> creerUtilisateur(@Valid @RequestBody utilisateurModel utilisateur) {
        utilisateurModel nouveau = service.creerUtilisateur(utilisateur);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    /**
     * READ ALL : Récupérer tous les utilisateurs
     * GET /api/utilisateurs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<utilisateurModel>> obtenirTousLesUtilisateurs() {
        List<utilisateurModel> liste = service.obtenirTousLesUtilisateurs();
        return ResponseEntity.ok(liste);
    }

    /**
     * READ ONE : Récupérer un utilisateur par son ID
     * GET /api/utilisateurs/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<utilisateurModel> obtenirUtilisateurParId(@PathVariable int id) {
        utilisateurModel utilisateur = service.obtenirUtilisateurParId(id);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * UPDATE : Mettre à jour un utilisateur
     * PUT /api/utilisateurs/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<utilisateurModel> mettreAJourUtilisateur(
            @PathVariable int id,
            @Valid @RequestBody utilisateurModel utilisateurModifie) {
        utilisateurModel maj = service.mettreAJourUtilisateur(id, utilisateurModifie);
        return ResponseEntity.ok(maj);
    }

    /**
     * DELETE : Supprimer un utilisateur
     * DELETE /api/utilisateurs/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable int id) {
        service.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
