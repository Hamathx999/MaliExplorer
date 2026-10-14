package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.dto.PartenariatSubmissionDTO;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.OpportuniteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/partenaires")
@RequiredArgsConstructor
@Tag(name = "Espace Partenaire", description = "Gestion des fiches et soumissions de partenariats par les partenaires (Artisans, Guides, Promoteurs)")
@SecurityRequirement(name = "Bearer Authentication")
public class PartenaireController {

    private final OpportuniteService opportuniteService;
    private final utilisateurRepository userRepository;
    private final com.maliexplorer_backend.repository.EvenementRepository evenementRepository;

    @PutMapping("/mon-projet")
    @PreAuthorize("hasRole('PARTENAIRE') or hasRole('ADMIN')")
    @Operation(summary = "Soumettre ou mettre à jour un projet / recherche de partenariat",
            description = "Active la recherche de partenariat et soumet le titre du projet et le besoin. Le projet passe automatiquement au statut EN_ATTENTE_VALIDATION pour modération admin.")
    public ResponseEntity<OpportuniteResponseDTO> soumettreProjet(
            @Valid @RequestBody PartenariatSubmissionDTO submissionDTO) {
        utilisateurModel currentUser = resolveCurrentUser();

        OpportuniteResponseDTO response = opportuniteService.soumettreProjetPartenaire(
                currentUser.getIdUsers(),
                submissionDTO.getTitreProjet(),
                submissionDTO.getBesoinPartenariat(),
                submissionDTO.getRecherchePartenariat() != null && submissionDTO.getRecherchePartenariat()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mon-projet")
    @PreAuthorize("hasRole('PARTENAIRE') or hasRole('ADMIN')")
    @Operation(summary = "Consulter l'état de modération de sa propre fiche partenaire")
    public ResponseEntity<OpportuniteResponseDTO> getMonProjet() {
        utilisateurModel currentUser = resolveCurrentUser();
        return ResponseEntity.ok(opportuniteService.getMonStatutPartenaire(currentUser.getIdUsers()));
    }

    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('PARTENAIRE') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques réelles du partenaire connecté")
    public ResponseEntity<java.util.Map<String, Object>> getStatistiques() {
        utilisateurModel currentUser = resolveCurrentUser();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        OpportuniteResponseDTO projet = opportuniteService.getMonStatutPartenaire(currentUser.getIdUsers());
        long vues = 0L;
        long commentaires = 0L;
        long publications = 0L;

        if (projet != null && projet.isRecherchePartenariat()) {
            publications++;
        }
        if (currentUser.getRole() == com.maliexplorer_backend.model.RoleModel.promoteur) {
            java.util.List<com.maliexplorer_backend.model.EvenementModel> events =
                    evenementRepository.findByEmailOrganisateur(currentUser.getEmail());
            publications += events.size();
            for (com.maliexplorer_backend.model.EvenementModel ev : events) {
                if (ev.getVues() != null) vues += ev.getVues();
                if (ev.getNombreCommentaires() != null) commentaires += ev.getNombreCommentaires();
            }
        }
        stats.put("vues", vues);
        stats.put("commentaires", commentaires);
        stats.put("publications", publications);
        stats.put("statutModeration", projet != null && projet.getStatutModeration() != null ? projet.getStatutModeration().name() : "VALIDE");
        return ResponseEntity.ok(stats);
    }

    private utilisateurModel resolveCurrentUser() {
        utilisateurModel currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            String email = SecurityUtils.getCurrentUserEmail();
            if (email != null) {
                currentUser = userRepository.findByEmail(email).orElse(null);
            }
        }
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }
        return currentUser;
    }
}
