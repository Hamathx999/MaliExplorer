package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;

import java.util.List;

public interface OpportuniteService {

    /**
     * Récupère la liste de toutes les fiches d'artisans, guides et promoteurs
     * avec "Recherche de partenariat" validées pour l'espace B2B et touristes.
     * Permet le filtrage optionnel par type et recherche par mot-clé.
     */
    List<OpportuniteResponseDTO> getOpportunitesValidees(String type, String keyword);

    /**
     * Récupère toutes les fiches soumises par les partenaires en attente de modération admin.
     */
    List<OpportuniteResponseDTO> getOpportunitesEnAttente();

    /**
     * Valide une fiche partenaire par l'administrateur.
     */
    OpportuniteResponseDTO validerOpportunite(int idUsers);

    /**
     * Rejette une fiche partenaire par l'administrateur avec motif optionnel.
     */
    OpportuniteResponseDTO rejeterOpportunite(int idUsers, String motifRejet);

    /**
     * Permet à un partenaire connecté d'activer/mettre à jour son statut "Recherche de partenariat"
     * et de soumettre son besoin (placé en attente de modération).
     */
    OpportuniteResponseDTO soumettreProjetPartenaire(int idUsers, String titreProjet, String besoinPartenariat, boolean recherchePartenariat);

    /**
     * Récupère le statut de partenariat de l'utilisateur partenaire connecté.
     */
    OpportuniteResponseDTO getMonStatutPartenaire(int idUsers);
}
