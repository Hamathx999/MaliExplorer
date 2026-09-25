package com.maliexplorer_backend.Services;

import com.maliexplorer_backend.Models.utilisateurModel;
import com.maliexplorer_backend.Repository.utilisateurRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class UtilisateurService {

    private final utilisateurRepository repository;

    public UtilisateurService(utilisateurRepository repository) {
        this.repository = repository;
    }

    /**
     * CREATE : Créer un nouvel utilisateur
     */
    public utilisateurModel creerUtilisateur(utilisateurModel utilisateur) {
        if (repository.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        // Si la date de création n'est pas définie, on met la date du jour
        if (utilisateur.getDateCreation() == null) {
            utilisateur.setDateCreation(Date.valueOf(LocalDate.now()));
        }

        return repository.save(utilisateur);
    }

    /**
     * READ ALL : Obtenir la liste de tous les utilisateurs
     */
    public List<utilisateurModel> obtenirTousLesUtilisateurs() {
        return repository.findAll();
    }

    /**
     * READ ONE : Obtenir un utilisateur par son identifiant
     */
    public utilisateurModel obtenirUtilisateurParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + id));
    }

    /**
     * UPDATE : Mettre à jour un utilisateur existant
     */
    public utilisateurModel mettreAJourUtilisateur(int id, utilisateurModel utilisateurModifie) {
        utilisateurModel existant = obtenirUtilisateurParId(id);

        // Vérifier si le nouvel email n'est pas déjà pris par un autre utilisateur
        if (!existant.getEmail().equalsIgnoreCase(utilisateurModifie.getEmail())
                && repository.existsByEmail(utilisateurModifie.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte !");
        }

        existant.setPrenom(utilisateurModifie.getPrenom());
        existant.setNom(utilisateurModifie.getNom());
        existant.setEmail(utilisateurModifie.getEmail());
        existant.setAdresse(utilisateurModifie.getAdresse());
        existant.setPhotoUrl(utilisateurModifie.getPhotoUrl());
        existant.setRole(utilisateurModifie.getRole());

        // Met à jour le mot de passe seulement s'il est renseigné
        if (utilisateurModifie.getMotDePasse() != null && !utilisateurModifie.getMotDePasse().isBlank()) {
            existant.setMotDePasse(utilisateurModifie.getMotDePasse());
        }

        return repository.save(existant);
    }

    /**
     * DELETE : Supprimer un utilisateur par son identifiant
     */
    public void supprimerUtilisateur(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : utilisateur introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }
}
