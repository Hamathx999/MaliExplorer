package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.LangueRequestDTO;
import com.maliexplorer_backend.dto.LangueResponseDTO;

import java.util.List;

/**
 * Interface de service métier pour la gestion des langues nationales du Mali.
 */
public interface LangueNationaleService {

    /**
     * Récupère la liste des langues publiées, triées par ordre d'affichage.
     */
    List<LangueResponseDTO> getPublishedLangues();

    /**
     * Récupère l'ensemble des langues nationales (publiées et brouillons) pour l'administration.
     */
    List<LangueResponseDTO> getAllLangues();

    /**
     * Récupère une langue par son identifiant unique.
     */
    LangueResponseDTO getLangueById(Long id);

    /**
     * Crée une nouvelle langue nationale.
     */
    LangueResponseDTO createLangue(LangueRequestDTO requestDTO);

    /**
     * Met à jour une langue nationale existante.
     */
    LangueResponseDTO updateLangue(Long id, LangueRequestDTO requestDTO);

    /**
     * Supprime une langue nationale.
     */
    void deleteLangue(Long id);

    /**
     * Bascule le statut de publication (publié / brouillon).
     */
    LangueResponseDTO togglePublication(Long id);
}
