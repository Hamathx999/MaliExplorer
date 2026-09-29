package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.GuideRequestDTO;
import com.maliexplorer_backend.dto.GuideResponseDTO;
import com.maliexplorer_backend.model.GuideModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.repository.GuideRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideService {

    private final GuideRepository repository;

    public GuideService(GuideRepository repository) {
        this.repository = repository;
    }

    public GuideResponseDTO creerGuide(GuideRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        GuideModel guide = new GuideModel();
        guide.setPrenom(request.getPrenom());
        guide.setNom(request.getNom());
        guide.setEmail(request.getEmail());
        guide.setMotDePasse(request.getMotDePasse());
        guide.setAdresse(request.getAdresse());
        guide.setPhotoUrl(request.getPhotoUrl());
        guide.setExperience(request.getExperience());
        guide.setDescription(request.getDescription());
        guide.setLangue(request.getLangue());
        guide.setPieceIdentite(request.getPieceIdentite());
        guide.setIdAdministrateur(request.getIdAdministrateur());
        guide.setRole(RoleModel.guide);
        guide.setDateCreation(Date.valueOf(LocalDate.now()));

        GuideModel saved = repository.save(guide);
        return mapToResponseDTO(saved);
    }

    public List<GuideResponseDTO> obtenirTousLesGuides() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public GuideResponseDTO obtenirGuideParId(int id) {
        GuideModel guide = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GuideModel introuvable avec l'ID : " + id));
        return mapToResponseDTO(guide);
    }

    public List<GuideResponseDTO> rechercherParLangue(String langue) {
        return repository.findByLangueContainingIgnoreCase(langue)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public GuideResponseDTO mettreAJourGuide(int id, GuideRequestDTO details) {
        GuideModel existant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GuideModel introuvable avec l'ID : " + id));

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

        GuideModel updated = repository.save(existant);
        return mapToResponseDTO(updated);
    }

    public void supprimerGuide(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("GuideModel introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    private GuideResponseDTO mapToResponseDTO(GuideModel model) {
        return GuideResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .idGuide(model.getIdGuide())
                .experience(model.getExperience())
                .description(model.getDescription())
                .langue(model.getLangue())
                .pieceIdentite(model.getPieceIdentite())
                .idAdministrateur(model.getIdAdministrateur())
                .build();
    }
}
