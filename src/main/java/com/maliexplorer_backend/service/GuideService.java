package com.maliexplorer_backend.service;

import com.maliexplorer_backend.model.Guide;
import com.maliexplorer_backend.model.Role;
import com.maliexplorer_backend.repository.GuideRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class GuideService {

    private final GuideRepository repository;

    public GuideService(GuideRepository repository) {
        this.repository = repository;
    }

    public Guide creerGuide(Guide guide) {
        if (repository.existsByEmail(guide.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }
        guide.setRole(Role.guide);
        if (guide.getDateCreation() == null) {
            guide.setDateCreation(Date.valueOf(LocalDate.now()));
        }
        return repository.save(guide);
    }

    public List<Guide> obtenirTousLesGuides() {
        return repository.findAll();
    }

    public Guide obtenirGuideParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide introuvable avec l'ID : " + id));
    }

    public List<Guide> rechercherParLangue(String langue) {
        return repository.findByLangueContainingIgnoreCase(langue);
    }

    public Guide mettreAJourGuide(int id, Guide details) {
        Guide existant = obtenirGuideParId(id);

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());
        existant.setExperience(details.getExperience());
        existant.setDescription(details.getDescription());
        existant.setLangue(details.getLangue());
        existant.setPieceIdentite(details.getPieceIdentite());
        existant.setIdAdministrateur(details.getIdAdministrateur());

        if (details.getMotDePasse() != null && !details.getMotDePasse().isBlank()) {
            existant.setMotDePasse(details.getMotDePasse());
        }

        return repository.save(existant);
    }

    public void supprimerGuide(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Guide introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }
}
