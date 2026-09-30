package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.AdministrateurRequestDTO;
import com.maliexplorer_backend.dto.AdministrateurResponseDTO;

import java.util.List;

public interface AdministrateurService {
    AdministrateurResponseDTO creerAdministrateur(AdministrateurRequestDTO request);
    List<AdministrateurResponseDTO> obtenirTousLesAdministrateurs();
    AdministrateurResponseDTO obtenirAdministrateurParId(int id);
    AdministrateurResponseDTO mettreAJourAdministrateur(int id, AdministrateurRequestDTO details);
    void supprimerAdministrateur(int id);
}
