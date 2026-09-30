package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.TouristeRequestDTO;
import com.maliexplorer_backend.dto.TouristeResponseDTO;

import java.util.List;

public interface TouristeService {
    TouristeResponseDTO creerTouriste(TouristeRequestDTO request);
    List<TouristeResponseDTO> obtenirTousLesTouristes();
    TouristeResponseDTO obtenirTouristeParId(int id);
    TouristeResponseDTO mettreAJourTouriste(int id, TouristeRequestDTO details);
    void supprimerTouriste(int id);
}
