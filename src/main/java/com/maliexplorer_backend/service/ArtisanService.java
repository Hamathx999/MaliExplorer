package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.ArtisanRequestDTO;
import com.maliexplorer_backend.dto.ArtisanResponseDTO;
import com.maliexplorer_backend.model.ArtisanModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.repository.ArtisanRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtisanService {

    private final ArtisanRepository repository;

    public ArtisanService(ArtisanRepository repository) {
        this.repository = repository;
    }

    public ArtisanResponseDTO creerArtisan(ArtisanRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        ArtisanModel artisan = new ArtisanModel();
        artisan.setPrenom(request.getPrenom());
        artisan.setNom(request.getNom());
        artisan.setEmail(request.getEmail());
        artisan.setMotDePasse(request.getMotDePasse());
        artisan.setAdresse(request.getAdresse());
        artisan.setPhotoUrl(request.getPhotoUrl());
        artisan.setTypeArtisanat(request.getTypeArtisanat());
        artisan.setRole(RoleModel.artisan);
        artisan.setDateCreation(Date.valueOf(LocalDate.now()));

        ArtisanModel saved = repository.save(artisan);
        return mapToResponseDTO(saved);
    }

    public List<ArtisanResponseDTO> obtenirTousLesArtisans() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ArtisanResponseDTO obtenirArtisanParId(int id) {
        ArtisanModel artisan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ArtisanModel introuvable avec l'ID : " + id));
        return mapToResponseDTO(artisan);
    }

    public List<ArtisanResponseDTO> rechercherParType(String typeArtisanat) {
        return repository.findByTypeArtisanatContainingIgnoreCase(typeArtisanat)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ArtisanResponseDTO mettreAJourArtisan(int id, ArtisanRequestDTO details) {
        ArtisanModel existant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ArtisanModel introuvable avec l'ID : " + id));

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

        ArtisanModel updated = repository.save(existant);
        return mapToResponseDTO(updated);
    }

    public void supprimerArtisan(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ArtisanModel introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    private ArtisanResponseDTO mapToResponseDTO(ArtisanModel model) {
        return ArtisanResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .typeArtisanat(model.getTypeArtisanat())
                .build();
    }
}
