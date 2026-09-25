package com.maliexplorer_backend.service;

import com.maliexplorer_backend.model.ArtisanModel;
import com.maliexplorer_backend.model.Role;
import com.maliexplorer_backend.repository.ArtisanRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class ArtisanService {

    private final ArtisanRepository repository;

    public ArtisanService(ArtisanRepository repository) {
        this.repository = repository;
    }

    public ArtisanModel creerArtisan(ArtisanModel artisan) {
        if (repository.existsByEmail(artisan.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }
        artisan.setRole(Role.artisan);
        if (artisan.getDateCreation() == null) {
            artisan.setDateCreation(Date.valueOf(LocalDate.now()));
        }
        return repository.save(artisan);
    }

    public List<ArtisanModel> obtenirTousLesArtisans() {
        return repository.findAll();
    }

    public ArtisanModel obtenirArtisanParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artisan introuvable avec l'ID : " + id));
    }

    public List<ArtisanModel> rechercherParType(String typeArtisanat) {
        return repository.findByTypeArtisanatContainingIgnoreCase(typeArtisanat);
    }

    public ArtisanModel mettreAJourArtisan(int id, ArtisanModel details) {
        ArtisanModel existant = obtenirArtisanParId(id);

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());
        existant.setTypeArtisanat(details.getTypeArtisanat());

        if (details.getMotDePasse() != null && !details.getMotDePasse().isBlank()) {
            existant.setMotDePasse(details.getMotDePasse());
        }

        return repository.save(existant);
    }

    public void supprimerArtisan(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Artisan introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }
}
