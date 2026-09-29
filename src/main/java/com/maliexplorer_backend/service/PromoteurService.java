package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PromoteurRequestDTO;
import com.maliexplorer_backend.dto.PromoteurResponseDTO;
import com.maliexplorer_backend.model.PromoteurModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.repository.PromoteurRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromoteurService {

    private final PromoteurRepository repository;

    public PromoteurService(PromoteurRepository repository) {
        this.repository = repository;
    }

    public PromoteurResponseDTO creerPromoteur(PromoteurRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        PromoteurModel promoteur = new PromoteurModel();
        promoteur.setPrenom(request.getPrenom());
        promoteur.setNom(request.getNom());
        promoteur.setEmail(request.getEmail());
        promoteur.setMotDePasse(request.getMotDePasse());
        promoteur.setAdresse(request.getAdresse());
        promoteur.setPhotoUrl(request.getPhotoUrl());
        promoteur.setNomOrganisation(request.getNomOrganisation());
        promoteur.setPieceIdentite(request.getPieceIdentite());
        promoteur.setRole(RoleModel.promoteur);
        promoteur.setDateCreation(Date.valueOf(LocalDate.now()));

        PromoteurModel saved = repository.save(promoteur);
        return mapToResponseDTO(saved);
    }

    public List<PromoteurResponseDTO> obtenirTousLesPromoteurs() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public PromoteurResponseDTO obtenirPromoteurParId(int id) {
        PromoteurModel promoteur = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PromoteurModel introuvable avec l'ID : " + id));
        return mapToResponseDTO(promoteur);
    }

    public List<PromoteurResponseDTO> rechercherParOrganisation(String nomOrganisation) {
        return repository.findByNomOrganisationContainingIgnoreCase(nomOrganisation)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public PromoteurResponseDTO mettreAJourPromoteur(int id, PromoteurRequestDTO details) {
        PromoteurModel existant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PromoteurModel introuvable avec l'ID : " + id));

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

        PromoteurModel updated = repository.save(existant);
        return mapToResponseDTO(updated);
    }

    public void supprimerPromoteur(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("PromoteurModel introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    private PromoteurResponseDTO mapToResponseDTO(PromoteurModel model) {
        return PromoteurResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .idPromoteur(model.getIdPromoteur())
                .nomOrganisation(model.getNomOrganisation())
                .pieceIdentite(model.getPieceIdentite())
                .build();
    }
}
