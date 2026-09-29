package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PromoteurRequestDTO;
import com.maliexplorer_backend.dto.PromoteurResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromoteurService {

    Page<PromoteurResponseDTO> obtenirTousLesPromoteurs(Pageable pageable);

    List<PromoteurResponseDTO> obtenirTousLesPromoteurs();

    PromoteurResponseDTO obtenirPromoteurParId(int id);

    Page<PromoteurResponseDTO> rechercherParOrganisation(String nomOrganisation, Pageable pageable);

    List<PromoteurResponseDTO> rechercherParOrganisation(String nomOrganisation);

    PromoteurResponseDTO creerPromoteur(PromoteurRequestDTO requestDTO);

    PromoteurResponseDTO mettreAJourPromoteur(int id, PromoteurRequestDTO requestDTO);

    void supprimerPromoteur(int id);

    PromoteurResponseDTO getProfilUtilisateurConnecte();

    PromoteurResponseDTO mettreAJourProfil(PromoteurRequestDTO requestDTO);
}
