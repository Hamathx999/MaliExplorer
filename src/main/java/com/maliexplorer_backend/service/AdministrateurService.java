package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.AdministrateurRequestDTO;
import com.maliexplorer_backend.dto.AdministrateurResponseDTO;
import com.maliexplorer_backend.model.AdministrateurModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.repository.AdministrateurRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdministrateurService {

    private final AdministrateurRepository repository;

    public AdministrateurService(AdministrateurRepository repository) {
        this.repository = repository;
    }

    public AdministrateurResponseDTO creerAdministrateur(AdministrateurRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        AdministrateurModel admin = new AdministrateurModel();
        admin.setPrenom(request.getPrenom());
        admin.setNom(request.getNom());
        admin.setEmail(request.getEmail());
        admin.setMotDePasse(request.getMotDePasse());
        admin.setAdresse(request.getAdresse());
        admin.setPhotoUrl(request.getPhotoUrl());
        admin.setRole(RoleModel.admin);
        admin.setDateCreation(Date.valueOf(LocalDate.now()));

        AdministrateurModel saved = repository.save(admin);
        return mapToResponseDTO(saved);
    }

    public List<AdministrateurResponseDTO> obtenirTousLesAdministrateurs() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public AdministrateurResponseDTO obtenirAdministrateurParId(int id) {
        AdministrateurModel admin = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AdministrateurModel introuvable avec l'ID : " + id));
        return mapToResponseDTO(admin);
    }

    public AdministrateurResponseDTO mettreAJourAdministrateur(int id, AdministrateurRequestDTO details) {
        AdministrateurModel existant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AdministrateurModel introuvable avec l'ID : " + id));

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());

        if (details.getMotDePasse() != null && !details.getMotDePasse().isBlank()) {
            existant.setMotDePasse(details.getMotDePasse());
        }

        AdministrateurModel updated = repository.save(existant);
        return mapToResponseDTO(updated);
    }

    public void supprimerAdministrateur(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("AdministrateurModel introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    private AdministrateurResponseDTO mapToResponseDTO(AdministrateurModel model) {
        return AdministrateurResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .build();
    }
}
