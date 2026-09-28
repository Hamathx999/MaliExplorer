package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.model.Administrateur;
import com.maliexplorer_backend.model.Role;
import com.maliexplorer_backend.repository.AdministrateurRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class AdministrateurService {

    private final AdministrateurRepository repository;

    public AdministrateurService(AdministrateurRepository repository) {
        this.repository = repository;
    }

    public Administrateur creerAdministrateur(Administrateur admin) {
        if (repository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }
        if (admin.getRole() == null) {
            admin.setRole(Role.admin);
        }
        if (admin.getDateCreation() == null) {
            admin.setDateCreation(Date.valueOf(LocalDate.now()));
        }
        return repository.save(admin);
    }

    public List<Administrateur> obtenirTousLesAdministrateurs() {
        return repository.findAll();
    }

    public Administrateur obtenirAdministrateurParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrateur introuvable avec l'ID : " + id));
    }

    public Administrateur mettreAJourAdministrateur(int id, Administrateur details) {
        Administrateur existant = obtenirAdministrateurParId(id);

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());

        if (details.getRole() != null) {
            existant.setRole(details.getRole());
        }

        if (details.getMotDePasse() != null && !details.getMotDePasse().isBlank()) {
            existant.setMotDePasse(details.getMotDePasse());
        }

        return repository.save(existant);
    }

    public void supprimerAdministrateur(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Administrateur introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }
}
