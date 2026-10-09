package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final utilisateurRepository repository;

    /**
     * CREATE : Créer un nouvel utilisateur
     */
    @Override
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
    @Override
    public List<utilisateurModel> obtenirTousLesUtilisateurs() {
        return repository.findAll();
    }

    /**
     * READ ONE : Obtenir un utilisateur par son identifiant
     */
    @Override
    public utilisateurModel obtenirUtilisateurParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("utilisateurModel", "id", id));
    }

    /**
     * UPDATE : Mettre à jour un utilisateur existant
     */
    @Override
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
        existant.setTelephone(utilisateurModifie.getTelephone());
        existant.setPhotoUrl(utilisateurModifie.getPhotoUrl());
        existant.setPieceIdentite(utilisateurModifie.getPieceIdentite());
        existant.setRole(utilisateurModifie.getRole());


        return repository.save(existant);
    }

    /**
     * DELETE : Supprimer un utilisateur par son identifiant
     */
    @Override
    public void supprimerUtilisateur(int id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("utilisateurModel", "id", id);
        }
        repository.deleteById(id);
    }
}
