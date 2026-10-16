package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.AdministrateurRequestDTO;
import com.maliexplorer_backend.dto.AdministrateurResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.AdministrateurModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.AdministrateurRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.AdministrateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministrateurServiceImpl implements AdministrateurService {

    private final AdministrateurRepository repository;
    private final utilisateurRepository userRepository;

    @Override
    public AdministrateurResponseDTO creerAdministrateur(AdministrateurRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email) || repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte !");
        }

        RoleModel role = request.getRole() != null ? request.getRole() : RoleModel.admin;
        RoleModel finalRole = (role == RoleModel.superAdmin) ? RoleModel.superAdmin : RoleModel.admin;

        // Création de l'utilisateur administrateur réel en base de données
        utilisateurModel user = utilisateurModel.builder()
                .prenom(request.getPrenom().trim())
                .nom(request.getNom().trim())
                .email(email)
                .telephone(request.getTelephone() != null ? request.getTelephone().trim() : "")
                .adresse(request.getAdresse() != null ? request.getAdresse().trim() : "")
                .photoUrl(request.getPhotoUrl() != null ? request.getPhotoUrl().trim() : "")
                .role(finalRole)
                .statut(request.getStatut() != null ? request.getStatut() : "ACTIF")
                .dateCreation(Date.valueOf(LocalDate.now()))
                .build();

        utilisateurModel saved = userRepository.save(user);
        return mapUserToDTO(saved);
    }

    @Override
    public List<AdministrateurResponseDTO> obtenirTousLesAdministrateurs() {
        // 1. Récupération dynamique de TOUS les administrateurs réels en base de données
        List<utilisateurModel> realAdmins = userRepository.findByRoleIn(List.of(RoleModel.admin, RoleModel.superAdmin));

        // 2. Récupération des entrées spécifiques de la table administrateurs
        List<AdministrateurModel> adminModels = repository.findAll();

        Map<String, AdministrateurResponseDTO> uniqueAdmins = new LinkedHashMap<>();

        // Traiter les utilisateurs réels
        for (utilisateurModel u : realAdmins) {
            if (u.getEmail() != null) {
                uniqueAdmins.put(u.getEmail().toLowerCase(), mapUserToDTO(u));
            }
        }

        // Compléter avec la table administrateurs si non déjà présent
        for (AdministrateurModel m : adminModels) {
            if (m.getEmail() != null && !uniqueAdmins.containsKey(m.getEmail().toLowerCase())) {
                uniqueAdmins.put(m.getEmail().toLowerCase(), mapAdminModelToDTO(m));
            }
        }

        return new ArrayList<>(uniqueAdmins.values());
    }

    @Override
    public AdministrateurResponseDTO obtenirAdministrateurParId(int id) {
        Optional<utilisateurModel> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && (userOpt.get().getRole() == RoleModel.admin || userOpt.get().getRole() == RoleModel.superAdmin)) {
            return mapUserToDTO(userOpt.get());
        }

        AdministrateurModel admin = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur", "id", id));
        return mapAdminModelToDTO(admin);
    }

    @Override
    public AdministrateurResponseDTO mettreAJourAdministrateur(int id, AdministrateurRequestDTO details) {
        String email = details.getEmail().trim().toLowerCase();

        // 1. Chercher d'abord dans userRepository
        Optional<utilisateurModel> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            utilisateurModel existant = userOpt.get();

            if (!existant.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Cet email est déjà pris par un autre compte !");
            }

            existant.setPrenom(details.getPrenom().trim());
            existant.setNom(details.getNom().trim());
            existant.setEmail(email);
            if (details.getTelephone() != null) existant.setTelephone(details.getTelephone().trim());
            if (details.getAdresse() != null) existant.setAdresse(details.getAdresse().trim());
            if (details.getPhotoUrl() != null) existant.setPhotoUrl(details.getPhotoUrl().trim());
            if (details.getRole() != null) {
                existant.setRole(details.getRole() == RoleModel.superAdmin ? RoleModel.superAdmin : RoleModel.admin);
            }
            if (details.getStatut() != null) existant.setStatut(details.getStatut());

            utilisateurModel updated = userRepository.save(existant);
            return mapUserToDTO(updated);
        }

        // 2. Sinon dans repository administrateurs
        AdministrateurModel adminModel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur", "id", id));

        if (!adminModel.getEmail().equalsIgnoreCase(email) && repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet email est déjà pris !");
        }

        adminModel.setPrenom(details.getPrenom().trim());
        adminModel.setNom(details.getNom().trim());
        adminModel.setEmail(email);
        if (details.getTelephone() != null) adminModel.setTelephone(details.getTelephone().trim());
        if (details.getAdresse() != null) adminModel.setAdresse(details.getAdresse().trim());
        if (details.getPhotoUrl() != null) adminModel.setPhotoUrl(details.getPhotoUrl().trim());
        if (details.getRole() != null) {
            adminModel.setRole(details.getRole() == RoleModel.superAdmin ? RoleModel.superAdmin : RoleModel.admin);
        }
        if (details.getStatut() != null) adminModel.setStatut(details.getStatut());

        AdministrateurModel updatedAdmin = repository.save(adminModel);
        return mapAdminModelToDTO(updatedAdmin);
    }

    @Override
    public void supprimerAdministrateur(int id) {
        boolean deleted = false;
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            deleted = true;
        }
        if (repository.existsById(id)) {
            repository.deleteById(id);
            deleted = true;
        }
        if (!deleted) {
            throw new ResourceNotFoundException("Administrateur", "id", id);
        }
    }

    private AdministrateurResponseDTO mapUserToDTO(utilisateurModel model) {
        return AdministrateurResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .telephone(model.getTelephone())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .statut(model.getStatut() != null ? model.getStatut() : "ACTIF")
                .build();
    }

    private AdministrateurResponseDTO mapAdminModelToDTO(AdministrateurModel model) {
        return AdministrateurResponseDTO.builder()
                .idUsers(model.getIdUsers())
                .firebaseUid(model.getFirebaseUid())
                .prenom(model.getPrenom())
                .nom(model.getNom())
                .email(model.getEmail())
                .telephone(model.getTelephone())
                .adresse(model.getAdresse())
                .photoUrl(model.getPhotoUrl())
                .dateCreation(model.getDateCreation())
                .role(model.getRole())
                .statut(model.getStatut() != null ? model.getStatut() : "ACTIF")
                .build();
    }
}
