package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PromoteurRequestDTO;
import com.maliexplorer_backend.dto.PromoteurResponseDTO;

import java.util.List;

public interface PromoteurService {
    PromoteurResponseDTO creerPromoteur(PromoteurRequestDTO request);
    List<PromoteurResponseDTO> obtenirTousLesPromoteurs();
    PromoteurResponseDTO obtenirPromoteurParId(int id);
    List<PromoteurResponseDTO> rechercherParOrganisation(String nomOrganisation);
    PromoteurResponseDTO mettreAJourPromoteur(int id, PromoteurRequestDTO details);
    void supprimerPromoteur(int id);
}
