package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.InfoPratiqueRequestDTO;
import com.maliexplorer_backend.dto.InfoPratiqueResponseDTO;

import java.util.List;

/**
 * Interface du service métier pour la gestion des informations pratiques du voyageur au Mali.
 */
public interface InfoPratiqueService {

    /**
     * Récupère la liste de toutes les fiches d'informations pratiques publiées.
     */
    List<InfoPratiqueResponseDTO> getPublishedInfosPratiques();

    /**
     * Récupère l'ensemble des fiches d'informations pratiques (publiées et brouillons) pour l'administration.
     */
    List<InfoPratiqueResponseDTO> getAllInfosPratiques();

    /**
     * Récupère les fiches publiées appartenant à une catégorie spécifique.
     */
    List<InfoPratiqueResponseDTO> getInfosPratiquesByCategorie(String categorie);

    /**
     * Récupère une fiche d'information pratique par son identifiant.
     */
    InfoPratiqueResponseDTO getInfoPratiqueById(Long id);

    /**
     * Crée une nouvelle fiche d'information pratique.
     */
    InfoPratiqueResponseDTO createInfoPratique(InfoPratiqueRequestDTO requestDTO);

    /**
     * Modifie une fiche d'information pratique existante.
     */
    InfoPratiqueResponseDTO updateInfoPratique(Long id, InfoPratiqueRequestDTO requestDTO);

    /**
     * Supprime une fiche d'information pratique.
     */
    void deleteInfoPratique(Long id);

    /**
     * Bascule le statut de publication d'une fiche.
     */
    InfoPratiqueResponseDTO togglePublication(Long id);
}
