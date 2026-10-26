package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.SimulationPaiementRequestDTO;
import com.maliexplorer_backend.dto.SimulationPaiementResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.CommandeBilletSimulationRepository;
import com.maliexplorer_backend.repository.CommentaireEvenementRepository;
import com.maliexplorer_backend.repository.EvenementRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Commentaires & Billetterie Simulation", description = "Gestion des retours touristes et simulation pédagogique d'achat de billets")
public class EvenementInteractionController {

    private final EvenementRepository evenementRepository;
    private final CommentaireEvenementRepository commentaireRepository;
    private final CommandeBilletSimulationRepository commandeRepository;
    private final utilisateurRepository userRepository;

    /**
     * Obtenir la liste des commentaires d'un événement
     */
    @GetMapping("/{id}/commentaires")
    @Operation(summary = "Lister les commentaires d'un événement")
    public ResponseEntity<List<CommentaireEvenementModel>> getCommentaires(@PathVariable Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Événement introuvable avec l'id " + id);
        }
        return ResponseEntity.ok(commentaireRepository.findByIdEvenementOrderByDateCreationDesc(id));
    }

    /**
     * Ajouter un commentaire sur un événement
     */
    @PostMapping("/{id}/commentaires")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Operation(summary = "Poster un avis / commentaire sur un événement")
    public ResponseEntity<CommentaireEvenementModel> ajouterCommentaire(
            @PathVariable Long id,
            @Valid @RequestBody CommentaireEvenementModel commentaire) {
        EvenementModel ev = evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id " + id));

        utilisateurModel user = SecurityUtils.getCurrentUser();
        if (user == null) {
            String email = SecurityUtils.getCurrentUserEmail();
            if (email != null) {
                user = userRepository.findByEmail(email).orElse(null);
            }
        }

        commentaire.setId(null);
        commentaire.setIdEvenement(id);
        if (user != null) {
            commentaire.setIdUser(user.getIdUsers());
            if (commentaire.getNomAuteur() == null || commentaire.getNomAuteur().isBlank()) {
                commentaire.setNomAuteur((user.getPrenom() + " " + user.getNom()).trim());
            }
            if (commentaire.getPhotoAuteur() == null || commentaire.getPhotoAuteur().isBlank()) {
                commentaire.setPhotoAuteur(user.getPhotoUrl());
            }
        } else if (commentaire.getNomAuteur() == null || commentaire.getNomAuteur().isBlank()) {
            commentaire.setNomAuteur("Visiteur MaliExplorer");
        }

        CommentaireEvenementModel saved = commentaireRepository.save(commentaire);

        // Mettre à jour le compteur sur l'événement
        long count = commentaireRepository.countByIdEvenement(id);
        ev.setNombreCommentaires(count);
        evenementRepository.save(ev);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Enregistrer une vue sur un événement
     */
    @PostMapping("/{id}/vues")
    @Transactional
    @Operation(summary = "Incrémenter le compteur de consultation réelle d'un événement")
    public ResponseEntity<Long> enregistrerVue(@PathVariable Long id) {
        EvenementModel ev = evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id " + id));
        long newVues = (ev.getVues() == null ? 0L : ev.getVues()) + 1L;
        ev.setVues(newVues);
        evenementRepository.save(ev);
        return ResponseEntity.ok(newVues);
    }

    /**
     * Simulation pédagogique d'achat de billets (Orange Money, Wave, Mastercard)
     */
    @PostMapping("/{id}/billets/simuler-achat")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Operation(summary = "Simuler l'achat de billets pour un événement (Orange Money, Wave, Mastercard)")
    public ResponseEntity<SimulationPaiementResponseDTO> simulerAchat(
            @PathVariable Long id,
            @Valid @RequestBody SimulationPaiementRequestDTO request) {
        EvenementModel ev = evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id " + id));

        utilisateurModel user = SecurityUtils.getCurrentUser();
        if (user == null) {
            String email = SecurityUtils.getCurrentUserEmail();
            if (email != null) {
                user = userRepository.findByEmail(email).orElse(null);
            }
        }

        double prixUnitaire = extrairePrix(ev.getPrix());
        double montantTotal = prixUnitaire * request.getQuantite();

        // Protection anti double-soumission (5 secondes)
        if (user != null) {
            List<CommandeBilletSimulationModel> recentes = commandeRepository.findByIdUserOrderByDateCreationDesc(user.getIdUsers());
            for (CommandeBilletSimulationModel existante : recentes) {
                if (id.equals(existante.getIdEvenement())
                        && existante.getQuantiteBillets() == request.getQuantite()
                        && existante.getCanalPaiement() == request.getCanalPaiement()
                        && existante.getDateCreation() != null
                        && existante.getDateCreation().isAfter(java.time.LocalDateTime.now().minusSeconds(5))) {
                    return ResponseEntity.ok(mapToResponseDTO(existante, ev));
                }
            }
        }

        String ref = "TKT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        CommandeBilletSimulationModel commande = CommandeBilletSimulationModel.builder()
                .referenceCommande(ref)
                .idEvenement(id)
                .titreEvenement(ev.getTitre())
                .dateDebutEvenement(ev.getDateDebut())
                .heureDebutEvenement(ev.getHeureDebut() != null && !ev.getHeureDebut().isBlank() ? ev.getHeureDebut() : "09:00")
                .dateFinEvenement(ev.getDateFin())
                .heureFinEvenement(ev.getHeureFin())
                .lieuEvenement(ev.getLieu())
                .villeEvenement(ev.getVille())
                .idUser(user != null ? user.getIdUsers() : null)
                .nomAcheteur(request.getNomAcheteur() != null && !request.getNomAcheteur().isBlank()
                        ? request.getNomAcheteur()
                        : (user != null ? (user.getPrenom() + " " + user.getNom()).trim() : "Participant MaliExplorer"))
                .emailAcheteur(request.getEmailAcheteur() != null ? request.getEmailAcheteur() : (user != null ? user.getEmail() : null))
                .telephoneAcheteur(request.getTelephone() != null ? request.getTelephone() : (user != null ? user.getTelephone() : null))
                .quantiteBillets(request.getQuantite())
                .prixUnitaire(prixUnitaire)
                .montantTotal(montantTotal)
                .devise("FCFA")
                .canalPaiement(request.getCanalPaiement())
                .statutSimulation(CommandeBilletSimulationModel.StatutSimulation.CONFIRMED)
                .emailPromoteur(ev.getEmailOrganisateur())
                .build();

        CommandeBilletSimulationModel saved = commandeRepository.save(commande);
        return new ResponseEntity<>(mapToResponseDTO(saved, ev), HttpStatus.CREATED);
    }

    /**
     * Récupérer les billets achetés par le touriste connecté ("Mes billets")
     */
    @GetMapping("/billets/mes-billets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lister les billets numériques de l'utilisateur connecté")
    public ResponseEntity<List<SimulationPaiementResponseDTO>> getMesBillets() {
        utilisateurModel user = SecurityUtils.getCurrentUser();
        if (user == null) {
            String email = SecurityUtils.getCurrentUserEmail();
            if (email != null) {
                user = userRepository.findByEmail(email).orElse(null);
            }
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        java.util.LinkedHashMap<Long, CommandeBilletSimulationModel> map = new java.util.LinkedHashMap<>();
        for (CommandeBilletSimulationModel c : commandeRepository.findByIdUserOrderByDateCreationDesc(user.getIdUsers())) {
            map.put(c.getId(), c);
        }
        if (user.getEmail() != null) {
            for (CommandeBilletSimulationModel c : commandeRepository.findByEmailAcheteurOrderByDateCreationDesc(user.getEmail())) {
                map.putIfAbsent(c.getId(), c);
            }
        }

        List<SimulationPaiementResponseDTO> dtos = map.values().stream()
                .map(c -> mapToResponseDTO(c, evenementRepository.findById(c.getIdEvenement()).orElse(null)))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Vérifier l'authenticité d'un billet numérique par sa référence (QR Code)
     */
    @GetMapping("/billets/verifier/{reference}")
    @Operation(summary = "Vérifier la validité d'un ticket numérique via sa référence QR")
    public ResponseEntity<SimulationPaiementResponseDTO> verifierTicket(@PathVariable String reference) {
        CommandeBilletSimulationModel commande = commandeRepository.findByReferenceCommande(reference.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Billet introuvable pour la référence : " + reference));
        EvenementModel ev = evenementRepository.findById(commande.getIdEvenement()).orElse(null);
        return ResponseEntity.ok(mapToResponseDTO(commande, ev));
    }

    /**
     * Lister les commandes de billets pour le promoteur connecté (uniquement ses propres événements)
     */
    @GetMapping("/billets/promoteur")
    @PreAuthorize("hasRole('PARTENAIRE') or hasRole('PROMOTEUR') or hasRole('ADMIN')")
    @Operation(summary = "Consulter les commandes de billets des événements du promoteur connecté")
    public ResponseEntity<List<SimulationPaiementResponseDTO>> getCommandesPromoteur() {
        if (SecurityUtils.isAdmin()) {
            List<SimulationPaiementResponseDTO> all = commandeRepository.findAllByOrderByDateCreationDesc().stream()
                    .map(c -> mapToResponseDTO(c, evenementRepository.findById(c.getIdEvenement()).orElse(null)))
                    .toList();
            return ResponseEntity.ok(all);
        }

        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<EvenementModel> mesEvents = evenementRepository.findByEmailOrganisateur(email);
        List<Long> mesEventIds = mesEvents.stream().map(EvenementModel::getId).toList();

        java.util.LinkedHashMap<Long, CommandeBilletSimulationModel> commandesMap = new java.util.LinkedHashMap<>();
        for (CommandeBilletSimulationModel c : commandeRepository.findByEmailPromoteurOrderByDateCreationDesc(email)) {
            commandesMap.put(c.getId(), c);
        }
        if (!mesEventIds.isEmpty()) {
            for (CommandeBilletSimulationModel c : commandeRepository.findByIdEvenementInOrderByDateCreationDesc(mesEventIds)) {
                commandesMap.putIfAbsent(c.getId(), c);
            }
        }

        List<SimulationPaiementResponseDTO> result = commandesMap.values().stream()
                .map(c -> mapToResponseDTO(c, evenementRepository.findById(c.getIdEvenement()).orElse(null)))
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * Lister toutes les commandes de billets de la plateforme (Admin uniquement)
     */
    @GetMapping("/billets/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consulter toutes les commandes de billets de la plateforme (Administrateur)")
    public ResponseEntity<List<SimulationPaiementResponseDTO>> getCommandesAdmin() {
        List<SimulationPaiementResponseDTO> all = commandeRepository.findAllByOrderByDateCreationDesc().stream()
                .map(c -> mapToResponseDTO(c, evenementRepository.findById(c.getIdEvenement()).orElse(null)))
                .toList();
        return ResponseEntity.ok(all);
    }

    private SimulationPaiementResponseDTO mapToResponseDTO(CommandeBilletSimulationModel c, EvenementModel ev) {
        String dateDebut = c.getDateDebutEvenement() != null && !c.getDateDebutEvenement().isBlank()
                ? c.getDateDebutEvenement()
                : (ev != null ? ev.getDateDebut() : null);
        String heureDebut = c.getHeureDebutEvenement() != null && !c.getHeureDebutEvenement().isBlank()
                ? c.getHeureDebutEvenement()
                : (ev != null && ev.getHeureDebut() != null && !ev.getHeureDebut().isBlank() ? ev.getHeureDebut() : "09:00");
        String dateFin = c.getDateFinEvenement() != null && !c.getDateFinEvenement().isBlank()
                ? c.getDateFinEvenement()
                : (ev != null ? ev.getDateFin() : null);
        String heureFin = c.getHeureFinEvenement() != null && !c.getHeureFinEvenement().isBlank()
                ? c.getHeureFinEvenement()
                : (ev != null ? ev.getHeureFin() : null);
        String lieu = c.getLieuEvenement() != null && !c.getLieuEvenement().isBlank()
                ? c.getLieuEvenement()
                : (ev != null ? ev.getLieu() : null);
        String ville = c.getVilleEvenement() != null && !c.getVilleEvenement().isBlank()
                ? c.getVilleEvenement()
                : (ev != null ? ev.getVille() : null);

        boolean isValid = c.getStatutSimulation() != CommandeBilletSimulationModel.StatutSimulation.CANCELLED;

        return SimulationPaiementResponseDTO.builder()
                .id(c.getId())
                .referenceCommande(c.getReferenceCommande())
                .idEvenement(c.getIdEvenement())
                .titreEvenement(c.getTitreEvenement() != null ? c.getTitreEvenement() : (ev != null ? ev.getTitre() : "Événement"))
                .dateDebutEvenement(dateDebut)
                .heureDebutEvenement(heureDebut)
                .dateFinEvenement(dateFin)
                .heureFinEvenement(heureFin)
                .lieuEvenement(lieu)
                .villeEvenement(ville)
                .nomAcheteur(c.getNomAcheteur())
                .quantiteBillets(c.getQuantiteBillets())
                .prixUnitaire(c.getPrixUnitaire())
                .montantTotal(c.getMontantTotal())
                .devise(c.getDevise() != null ? c.getDevise() : "FCFA")
                .canalPaiement(c.getCanalPaiement())
                .statutSimulation(c.getStatutSimulation())
                .valide(isValid)
                .message(isValid
                        ? "Commande confirmée (Mode test — " + c.getCanalPaiement().name() + ")"
                        : "Commande annulée")
                .dateCreation(c.getDateCreation())
                .build();
    }

    private double extrairePrix(String prixStr) {
        if (prixStr == null || prixStr.isBlank() || prixStr.toLowerCase().contains("gratuit")) {
            return 0.0;
        }
        try {
            Pattern p = Pattern.compile("(\\d[\\d\\s.,]*)");
            Matcher m = p.matcher(prixStr.replace(" ", "").replace("\u00a0", ""));
            if (m.find()) {
                String clean = m.group(1).replace(",", ".");
                return Double.parseDouble(clean);
            }
        } catch (Exception ignored) {}
        return 2000.0; // Prix de base par défaut en FCFA
    }
}

