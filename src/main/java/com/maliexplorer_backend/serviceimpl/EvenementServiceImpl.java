package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.EvenementModel;
import com.maliexplorer_backend.model.PromoteurModel;
import com.maliexplorer_backend.model.StatutModeration;
import com.maliexplorer_backend.repository.EvenementRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.service.EvenementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository repository;
    private final PromoteurRepository promoteurRepository;

    public EvenementServiceImpl(EvenementRepository repository) {
        this.repository = repository;
        this.promoteurRepository = null;
    }

    @org.springframework.beans.factory.annotation.Autowired
    public EvenementServiceImpl(EvenementRepository repository, PromoteurRepository promoteurRepository) {
        this.repository = repository;
        this.promoteurRepository = promoteurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvenementModel> getAll() {
        return repository.findAllByOrderByDateSoumissionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvenementModel> getByStatut(EvenementModel.Statut statut) {
        return repository.findByStatutOrderByDateSoumissionDesc(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvenementModel> getMesEvenements(String email) {
        if (email == null || email.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return repository.findByEmailOrganisateur(email.trim());
    }

    @Override
    public EvenementModel getById(Long id) {
        EvenementModel e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id " + id));
        e.setVues(e.getVues() == null ? 1L : e.getVues() + 1);
        return repository.save(e);
    }

    @Override
    public EvenementModel create(EvenementModel evenement) {
        evenement.setId(null);
        evenement.setMotifRejet(null);
        validerCohérenceDatesEtHeures(evenement);

        String email = SecurityUtils.getCurrentUserEmail();
        com.maliexplorer_backend.model.utilisateurModel currentUser = SecurityUtils.getCurrentUser();

        if (!SecurityUtils.isAdmin() && email != null && !email.isBlank()) {
            evenement.setEmailOrganisateur(email);
        } else if (evenement.getEmailOrganisateur() == null || evenement.getEmailOrganisateur().isBlank()) {
            evenement.setEmailOrganisateur(email);
        }

        if ((evenement.getNomOrganisateur() == null || evenement.getNomOrganisateur().isBlank()) && currentUser != null) {
            String org = currentUser.getNomOrganisation();
            if (org == null || org.isBlank()) {
                org = ((currentUser.getPrenom() != null ? currentUser.getPrenom() + " " : "")
                        + (currentUser.getNom() != null ? currentUser.getNom() : "")).trim();
            }
            evenement.setNomOrganisateur(org);
        }

        boolean publicationDirecte = false;
        if (SecurityUtils.isAdmin()) {
            publicationDirecte = true;
            log.info("Publication directe de l'événement accordée (utilisateur Administrateur)");
        } else if (promoteurRepository != null && email != null) {
            Optional<PromoteurModel> promoteurOpt = promoteurRepository.findByEmail(email);
            if (promoteurOpt.isPresent()) {
                PromoteurModel promoteur = promoteurOpt.get();
                if (promoteur.getStatutModeration() == StatutModeration.VALIDE) {
                    publicationDirecte = true;
                    log.info("Publication directe de l'événement accordée pour le promoteur validé : {}", email);
                }
            }
        }
        if (!publicationDirecte && currentUser != null && currentUser.getStatutModeration() == StatutModeration.VALIDE) {
            publicationDirecte = true;
        }

        if (publicationDirecte) {
            evenement.setStatut(EvenementModel.Statut.VALIDE);
        } else {
            evenement.setStatut(EvenementModel.Statut.EN_ATTENTE);
        }

        return repository.save(evenement);
    }

    @Override
    public EvenementModel update(Long id, EvenementModel data) {
        EvenementModel e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id " + id));

        if (!SecurityUtils.isAdmin()) {
            String currentEmail = SecurityUtils.getCurrentUserEmail();
            if (currentEmail == null || e.getEmailOrganisateur() == null
                    || !currentEmail.equalsIgnoreCase(e.getEmailOrganisateur().trim())) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "Vous n'êtes autorisé à modifier que vos propres événements.");
            }
        }

        validerCohérenceDatesEtHeures(data);

        if (data.getTitre() != null && !data.getTitre().isBlank()) e.setTitre(data.getTitre().trim());
        if (data.getDescription() != null) e.setDescription(data.getDescription());
        if (data.getNomOrganisateur() != null && !data.getNomOrganisateur().isBlank()) e.setNomOrganisateur(data.getNomOrganisateur());
        if (SecurityUtils.isAdmin() && data.getEmailOrganisateur() != null && !data.getEmailOrganisateur().isBlank()) {
            e.setEmailOrganisateur(data.getEmailOrganisateur());
        }
        if (data.getTelephoneOrganisateur() != null) e.setTelephoneOrganisateur(data.getTelephoneOrganisateur());
        if (data.getDateDebut() != null) e.setDateDebut(data.getDateDebut());
        if (data.getDateFin() != null) e.setDateFin(data.getDateFin());
        if (data.getHeureDebut() != null) e.setHeureDebut(data.getHeureDebut());
        if (data.getHeureFin() != null) e.setHeureFin(data.getHeureFin());
        if (data.getLieu() != null) e.setLieu(data.getLieu());
        if (data.getVille() != null) e.setVille(data.getVille());
        if (data.getRegion() != null) e.setRegion(data.getRegion());
        if (data.getCategorie() != null) e.setCategorie(data.getCategorie());
        if (data.getPrix() != null) e.setPrix(data.getPrix());
        if (data.getImageUrl() != null && !data.getImageUrl().isBlank()) e.setImageUrl(data.getImageUrl());
        if (data.getAfficheUrl() != null && !data.getAfficheUrl().isBlank()) e.setAfficheUrl(data.getAfficheUrl());
        if (SecurityUtils.isAdmin() && data.getStatut() != null) {
            e.setStatut(data.getStatut());
        }
        return repository.save(e);
    }

    private void validerCohérenceDatesEtHeures(EvenementModel ev) {
        if (ev == null) return;
        java.time.LocalDate debut = parseIsoOrFrDate(ev.getDateDebut());
        java.time.LocalDate fin = parseIsoOrFrDate(ev.getDateFin());
        if (debut != null && fin != null) {
            if (fin.isBefore(debut)) {
                throw new com.maliexplorer_backend.exception.BadRequestException(
                        "La date de fin ne peut pas être antérieure à la date de début.");
            }
            if (fin.isEqual(debut) && ev.getHeureDebut() != null && ev.getHeureFin() != null
                    && !ev.getHeureDebut().isBlank() && !ev.getHeureFin().isBlank()) {
                try {
                    java.time.LocalTime tDebut = java.time.LocalTime.parse(ev.getHeureDebut().trim());
                    java.time.LocalTime tFin = java.time.LocalTime.parse(ev.getHeureFin().trim());
                    if (tFin.isBefore(tDebut)) {
                        throw new com.maliexplorer_backend.exception.BadRequestException(
                                "L'heure de fin ne peut pas être antérieure à l'heure de début pour un événement sur le même jour.");
                    }
                } catch (java.time.format.DateTimeParseException ignored) {}
            }
        }
    }

    private java.time.LocalDate parseIsoOrFrDate(String d) {
        if (d == null || d.isBlank()) return null;
        String clean = d.trim();
        for (java.time.format.DateTimeFormatter fmt : List.of(
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE,
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"))) {
            try {
                return java.time.LocalDate.parse(clean, fmt);
            } catch (Exception ignored) {}
        }
        return null;
    }

    @Override
    public EvenementModel approuver(Long id) {
        EvenementModel e = getById(id);
        e.setStatut(EvenementModel.Statut.APPROUVE);
        e.setMotifRejet(null);
        return repository.save(e);
    }

    @Override
    public EvenementModel rejeter(Long id, String motif) {
        EvenementModel e = getById(id);
        e.setStatut(EvenementModel.Statut.REFUSE);
        e.setMotifRejet(motif);
        return repository.save(e);
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
    }
}

