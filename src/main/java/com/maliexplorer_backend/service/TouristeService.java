package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.TouristeRequestDTO;
import com.maliexplorer_backend.dto.TouristeResponseDTO;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.TouristeModel;
import com.maliexplorer_backend.repository.TouristeRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TouristeService {

    private final TouristeRepository repository;

    public TouristeService(TouristeRepository repository) {
        this.repository = repository;
    }

    public TouristeResponseDTO creerTouriste(TouristeRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        TouristeModel touriste = new TouristeModel();
        touriste.setPrenom(request.getPrenom());
        touriste.setNom(request.getNom());
        touriste.setEmail(request.getEmail());
        touriste.setMotDePasse(request.getMotDePasse());
        touriste.setAdresse(request.getAdresse());
        touriste.setPhotoUrl(request.getPhotoUrl());
        touriste.setPoints(request.getPoints());
        touriste.setRole(RoleModel.touriste);
        touriste.setDateCreation(Date.valueOf(LocalDate.now()));

        TouristeModel saved = repository.save(touriste);
        return mapToResponseDTO(saved);
    }

    public List<TouristeResponseDTO> obtenirTousLesTouristes() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public TouristeResponseDTO obtenirTouristeParId(int id) {
        TouristeModel touriste = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TouristeModel introuvable avec l'ID : " + id));
        return mapToResponseDTO(touriste);
    }

    public TouristeResponseDTO mettreAJourTouriste(int id, TouristeRequestDTO details) {
        TouristeModel existant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TouristeModel introuvable avec l'ID : " + id));

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

        TouristeModel updated = repository.save(existant);
        return mapToResponseDTO(updated);
    }

    public void supprimerTouriste(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("TouristeModel introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    private TouristeResponseDTO mapToResponseDTO(TouristeModel model) {
        return TouristeResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .points(model.getPoints())
                .build();
    }
}
