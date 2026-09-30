package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.HistoriquePointResponseDTO;
import com.maliexplorer_backend.dto.ProgressionResponseDTO;
import com.maliexplorer_backend.model.utilisateurModel;

import java.util.List;

/**
 * Service gérant le système de points et de progression des badges Bambara.
 * Centralise tous les calculs côté Spring Boot (anti-triche / anti-duplication).
 */
public interface BadgeProgressionService {

    ProgressionResponseDTO getProgressionUtilisateur(int userId);

    ProgressionResponseDTO getProgressionCurrentUtilisateur();

    List<HistoriquePointResponseDTO> getHistoriquePoints(int userId);

    List<HistoriquePointResponseDTO> getHistoriquePointsCurrentUtilisateur();

    ProgressionResponseDTO attribuerPoints(int userId, String action, int points, String description, String referenceActivite);

    ProgressionResponseDTO attribuerPointsUtilisateurConnecte(String action, int points, String description, String referenceActivite);

    boolean aDejaValideActivite(int userId, String referenceActivite);

    ProgressionResponseDTO calculerProgression(int points, utilisateurModel user);
}
