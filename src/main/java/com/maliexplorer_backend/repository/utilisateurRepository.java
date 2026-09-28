package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.utilisateurModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface utilisateurRepository extends JpaRepository<utilisateurModel, Integer> {

    // Méthodes utiles pour le CRUD et la vérification d'unicité
    Optional<utilisateurModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
