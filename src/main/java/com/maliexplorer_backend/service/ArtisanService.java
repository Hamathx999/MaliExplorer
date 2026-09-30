package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.ArtisanRequestDTO;
import com.maliexplorer_backend.dto.ArtisanResponseDTO;

import java.util.List;

public interface ArtisanService {
    ArtisanResponseDTO creerArtisan(ArtisanRequestDTO request);
    List<ArtisanResponseDTO> obtenirTousLesArtisans();
    ArtisanResponseDTO obtenirArtisanParId(int id);
    List<ArtisanResponseDTO> rechercherParType(String typeArtisanat);
    ArtisanResponseDTO mettreAJourArtisan(int id, ArtisanRequestDTO details);
    void supprimerArtisan(int id);
}
