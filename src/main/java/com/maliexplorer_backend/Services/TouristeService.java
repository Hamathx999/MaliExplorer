package com.maliexplorer_backend.Services;

import com.maliexplorer_backend.Models.Role;
import com.maliexplorer_backend.Models.TouristeModel;
import com.maliexplorer_backend.Repository.TouristeRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class TouristeService {

    private final TouristeRepository repository;

    public TouristeService(TouristeRepository repository) {
        this.repository = repository;
    }

    public TouristeModel creerTouriste(TouristeModel touriste) {
        if (repository.existsByEmail(touriste.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }
        touriste.setRole(Role.touriste);
        if (touriste.getDateCreation() == null) {
            touriste.setDateCreation(Date.valueOf(LocalDate.now()));
        }
        return repository.save(touriste);
    }

    public List<TouristeModel> obtenirTousLesTouristes() {
        return repository.findAll();
    }

    public TouristeModel obtenirTouristeParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Touriste introuvable avec l'ID : " + id));
    }

    public TouristeModel mettreAJourTouriste(int id, TouristeModel details) {
        TouristeModel existant = obtenirTouristeParId(id);

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());
        existant.setPoints(details.getPoints());

        if (details.getMotDePasse() != null && !details.getMotDePasse().isBlank()) {
            existant.setMotDePasse(details.getMotDePasse());
        }

        return repository.save(existant);
    }

    public void supprimerTouriste(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Touriste introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }
}
