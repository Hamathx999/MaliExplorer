package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.TouristeRequestDTO;
import com.maliexplorer_backend.dto.TouristeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TouristeService {

    Page<TouristeResponseDTO> obtenirTousLesTouristes(Pageable pageable);

    List<TouristeResponseDTO> obtenirTousLesTouristes();

    TouristeResponseDTO obtenirTouristeParId(int id);

    TouristeResponseDTO creerTouriste(TouristeRequestDTO requestDTO);

    TouristeResponseDTO mettreAJourTouriste(int id, TouristeRequestDTO requestDTO);

    void supprimerTouriste(int id);

    TouristeResponseDTO getProfilUtilisateurConnecte();

    TouristeResponseDTO mettreAJourProfil(TouristeRequestDTO requestDTO);

    TouristeResponseDTO ajouterLieuVisite(Long idLieu);

    TouristeResponseDTO ajouterArticleLu(Long idArticle);
}
