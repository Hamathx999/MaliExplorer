package com.maliexplorer_backend.service;

import com.maliexplorer_backend.model.PromoteurModel;
import com.maliexplorer_backend.model.Role;
import com.maliexplorer_backend.repository.PromoteurRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class PromoteurService {

    private final PromoteurRepository repository;

    public PromoteurService(PromoteurRepository repository) {
        this.repository = repository;
    }

    public PromoteurModel creerPromoteur(PromoteurModel promoteur) {
        if (repository.existsByEmail(promoteur.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }
        promoteur.setRole(Role.promoteur);
        if (promoteur.getDateCreation() == null) {
            promoteur.setDateCreation(Date.valueOf(LocalDate.now()));
        }
        return repository.save(promoteur);
    }

    public List<PromoteurModel> obtenirTousLesPromoteurs() {
        return repository.findAll();
    }

    public PromoteurModel obtenirPromoteurParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoteur introuvable avec l'ID : " + id));
    }

    public List<PromoteurModel> rechercherParOrganisation(String nomOrganisation) {
        return repository.findByNomOrganisationContainingIgnoreCase(nomOrganisation);
    }

    public PromoteurModel mettreAJourPromoteur(int id, PromoteurModel details) {
        PromoteurModel existant = obtenirPromoteurParId(id);

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());
        existant.setNomOrganisation(details.getNomOrganisation());
        existant.setPieceIdentite(details.getPieceIdentite());

        if (details.getMotDePasse() != null && !details.getMotDePasse().isBlank()) {
            existant.setMotDePasse(details.getMotDePasse());
        }

        return repository.save(existant);
    }

    public void supprimerPromoteur(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Promoteur introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }
}
