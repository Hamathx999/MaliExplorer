package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.FavoriResponseDTO;

import java.util.List;

public interface FavoriService {

    FavoriResponseDTO ajouterLieuFavori(Long lieuId);

    void supprimerLieuFavori(Long lieuId);

    FavoriResponseDTO ajouterArticleFavori(Long articleId);

    void supprimerArticleFavori(Long articleId);

    List<FavoriResponseDTO> getMesFavoris();

    List<FavoriResponseDTO> getMesFavorisLieux();

    List<FavoriResponseDTO> getMesFavorisArticles();

    boolean estLieuFavori(Long lieuId);

    boolean estArticleFavori(Long articleId);
}
