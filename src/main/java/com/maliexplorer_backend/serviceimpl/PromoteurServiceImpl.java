package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.PromoteurRequestDTO;
import com.maliexplorer_backend.dto.PromoteurResponseDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.PromoteurModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.service.PromoteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoteurServiceImpl implements PromoteurService {

    private final PromoteurRepository promoteurRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PromoteurResponseDTO> obtenirTousLesPromoteurs(Pageable pageable) {
        return promoteurRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoteurResponseDTO> obtenirTousLesPromoteurs() {
        return promoteurRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromoteurResponseDTO obtenirPromoteurParId(int id) {
        PromoteurModel promoteur = findPromoteurOrThrow(id);
        return mapToResponseDTO(promoteur);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromoteurResponseDTO> rechercherParOrganisation(String nomOrganisation, Pageable pageable) {
        return promoteurRepository.findByNomOrganisationContainingIgnoreCase(nomOrganisation, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoteurResponseDTO> rechercherParOrganisation(String nomOrganisation) {
        return promoteurRepository.findByNomOrganisationContainingIgnoreCase(nomOrganisation)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PromoteurResponseDTO creerPromoteur(PromoteurRequestDTO requestDTO) {
        if (promoteurRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Cet email est déjà associé à un compte !");
        }

        PromoteurModel promoteur = new PromoteurModel();
        promoteur.setPrenom(requestDTO.getPrenom());
        promoteur.setNom(requestDTO.getNom());
        promoteur.setEmail(requestDTO.getEmail());
        promoteur.setMotDePasse(requestDTO.getMotDePasse());
        promoteur.setAdresse(requestDTO.getAdresse());
        promoteur.setPhotoUrl(requestDTO.getPhotoUrl());
        promoteur.setNomOrganisation(requestDTO.getNomOrganisation());
        promoteur.setPieceIdentite(requestDTO.getPieceIdentite());
        promoteur.setRole(RoleModel.promoteur);
        promoteur.setDateCreation(Date.valueOf(LocalDate.now()));

        PromoteurModel saved = promoteurRepository.save(promoteur);
        return mapToResponseDTO(saved);
    }

    @Override
    public PromoteurResponseDTO mettreAJourPromoteur(int id, PromoteurRequestDTO requestDTO) {
        PromoteurModel existant = findPromoteurOrThrow(id);

        if (!existant.getEmail().equalsIgnoreCase(requestDTO.getEmail())
                && promoteurRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Cet email est déjà pris !");
        }

        existant.setPrenom(requestDTO.getPrenom());
        existant.setNom(requestDTO.getNom());
        existant.setEmail(requestDTO.getEmail());
        existant.setAdresse(requestDTO.getAdresse());
        existant.setPhotoUrl(requestDTO.getPhotoUrl());
        existant.setNomOrganisation(requestDTO.getNomOrganisation());
        existant.setPieceIdentite(requestDTO.getPieceIdentite());

        if (requestDTO.getMotDePasse() != null && !requestDTO.getMotDePasse().isBlank()) {
            existant.setMotDePasse(requestDTO.getMotDePasse());
        }

        PromoteurModel updated = promoteurRepository.save(existant);
        return mapToResponseDTO(updated);
    }

    @Override
    public void supprimerPromoteur(int id) {
        if (!promoteurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promoteur introuvable avec l'ID : " + id);
        }
        promoteurRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PromoteurResponseDTO getProfilUtilisateurConnecte() {
        PromoteurModel current = getCurrentPromoteurOrThrow();
        return mapToResponseDTO(current);
    }

    @Override
    public PromoteurResponseDTO mettreAJourProfil(PromoteurRequestDTO requestDTO) {
        PromoteurModel current = getCurrentPromoteurOrThrow();

        if (requestDTO.getPrenom() != null && !requestDTO.getPrenom().isBlank()) {
            current.setPrenom(requestDTO.getPrenom());
        }
        if (requestDTO.getNom() != null && !requestDTO.getNom().isBlank()) {
            current.setNom(requestDTO.getNom());
        }
        if (requestDTO.getAdresse() != null) {
            current.setAdresse(requestDTO.getAdresse());
        }
        if (requestDTO.getPhotoUrl() != null) {
            current.setPhotoUrl(requestDTO.getPhotoUrl());
        }
        if (requestDTO.getNomOrganisation() != null && !requestDTO.getNomOrganisation().isBlank()) {
            current.setNomOrganisation(requestDTO.getNomOrganisation());
        }
        if (requestDTO.getPieceIdentite() != null) {
            current.setPieceIdentite(requestDTO.getPieceIdentite());
        }

        PromoteurModel updated = promoteurRepository.save(current);
        return mapToResponseDTO(updated);
    }

    private PromoteurModel findPromoteurOrThrow(int id) {
        return promoteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoteur introuvable avec l'ID : " + id));
    }

    private PromoteurModel getCurrentPromoteurOrThrow() {
        String email = SecurityUtils.getCurrentUserEmail()
                .orElseThrow(() -> new BadRequestException("Aucun utilisateur authentifié dans la session"));

        return promoteurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profil promoteur introuvable pour l'utilisateur connecté : " + email));
    }

    private PromoteurResponseDTO mapToResponseDTO(PromoteurModel p) {
        return PromoteurResponseDTO.builder()
                .idUsers(p.getIdUsers())
                .idPromoteur(p.getIdPromoteur())
                .prenom(p.getPrenom())
                .nom(p.getNom())
                .email(p.getEmail())
                .adresse(p.getAdresse())
                .photoUrl(p.getPhotoUrl())
                .dateCreation(p.getDateCreation())
                .role(p.getRole())
                .nomOrganisation(p.getNomOrganisation())
                .pieceIdentite(p.getPieceIdentite())
                .build();
    }
}
