package com.maliexplorer_backend.service;

import com.maliexplorer_backend.model.utilisateurModel;

import java.util.List;

public interface UtilisateurService {
    /**
     * CREATE : Créer un nouvel utilisateur
     */
    utilisateurModel creerUtilisateur(utilisateurModel utilisateur);

    /**
     * READ ALL : Obtenir la liste de tous les utilisateurs
     */
    List<utilisateurModel> obtenirTousLesUtilisateurs();

    /**
     * READ ONE : Obtenir un utilisateur par son identifiant
     */
    utilisateurModel obtenirUtilisateurParId(int id);

    /**
     * UPDATE : Mettre à jour un utilisateur existant
     */
    utilisateurModel mettreAJourUtilisateur(int id, utilisateurModel utilisateurModifie);

    /**
     * DELETE : Supprimer un utilisateur par son identifiant
     */
    void supprimerUtilisateur(int id);
}
