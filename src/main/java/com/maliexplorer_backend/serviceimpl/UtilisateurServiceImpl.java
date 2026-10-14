package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.UtilisateurService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final utilisateurRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

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

        if (utilisateurModifie.getPrenom() != null) existant.setPrenom(utilisateurModifie.getPrenom());
        if (utilisateurModifie.getNom() != null) existant.setNom(utilisateurModifie.getNom());
        if (utilisateurModifie.getEmail() != null) existant.setEmail(utilisateurModifie.getEmail());
        if (utilisateurModifie.getAdresse() != null) existant.setAdresse(utilisateurModifie.getAdresse());
        if (utilisateurModifie.getPhotoUrl() != null) existant.setPhotoUrl(utilisateurModifie.getPhotoUrl());
        if (utilisateurModifie.getRole() != null) existant.setRole(utilisateurModifie.getRole());
        if (utilisateurModifie.getStatut() != null && !utilisateurModifie.getStatut().isBlank()) {
            existant.setStatut(utilisateurModifie.getStatut());
        }
        existant.setPoints(utilisateurModifie.getPoints());

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

        try {
            entityManager.createNativeQuery("DELETE FROM favoris WHERE id_users = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM touriste_quiz WHERE touriste_id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM historique_points WHERE touriste_id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM touriste WHERE id_users = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM guide WHERE id_users = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM artisan WHERE id_users = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM promoteur WHERE id_users = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        try {
            entityManager.createNativeQuery("DELETE FROM administrateur WHERE id_users = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {}

        repository.deleteById(id);
    }
}
