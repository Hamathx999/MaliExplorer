package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.GuideRequestDTO;
import com.maliexplorer_backend.dto.GuideResponseDTO;

import java.util.List;

public interface GuideService {
    GuideResponseDTO creerGuide(GuideRequestDTO request);
    List<GuideResponseDTO> obtenirTousLesGuides();
    GuideResponseDTO obtenirGuideParId(int id);
    List<GuideResponseDTO> rechercherParLangue(String langue);
    GuideResponseDTO mettreAJourGuide(int id, GuideRequestDTO details);
    void supprimerGuide(int id);
}
