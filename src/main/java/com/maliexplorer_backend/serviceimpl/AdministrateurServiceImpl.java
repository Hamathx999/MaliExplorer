package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.AdministrateurRequestDTO;
import com.maliexplorer_backend.dto.AdministrateurResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.AdministrateurModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.repository.AdministrateurRepository;
import com.maliexplorer_backend.service.AdministrateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministrateurServiceImpl implements AdministrateurService {

    private final AdministrateurRepository repository;

    @Override
    public AdministrateurResponseDTO creerAdministrateur(AdministrateurRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        AdministrateurModel admin = new AdministrateurModel();
        admin.setPrenom(request.getPrenom());
        admin.setNom(request.getNom());
        admin.setEmail(request.getEmail());
        admin.setAdresse(request.getAdresse());
        admin.setPhotoUrl(request.getPhotoUrl());
        admin.setRole(RoleModel.admin);
        admin.setDateCreation(Date.valueOf(LocalDate.now()));

        AdministrateurModel saved = repository.save(admin);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<AdministrateurResponseDTO> obtenirTousLesAdministrateurs() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdministrateurResponseDTO obtenirAdministrateurParId(int id) {
        AdministrateurModel admin = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdministrateurModel", "id", id));
        return mapToResponseDTO(admin);
    }

    @Override
    public AdministrateurResponseDTO mettreAJourAdministrateur(int id, AdministrateurRequestDTO details) {
        AdministrateurModel existant = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdministrateurModel", "id", id));

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && repository.existsByEmail(details.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        existant.setPrenom(details.getPrenom());
        existant.setNom(details.getNom());
        existant.setEmail(details.getEmail());
        existant.setAdresse(details.getAdresse());
        existant.setPhotoUrl(details.getPhotoUrl());

        AdministrateurModel updated = repository.save(existant);
        return mapToResponseDTO(updated);
    }

    @Override
    public void supprimerAdministrateur(int id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("AdministrateurModel", "id", id);
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
