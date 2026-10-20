package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.LangueNationaleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Référentiel Spring Data JPA pour l'accès aux données des langues nationales du Mali.
 */
@Repository
public interface LangueNationaleRepository extends JpaRepository<LangueNationaleModel, Long> {

    /**
     * Récupère toutes les langues nationales publiées, triées par ordre d'affichage croissant.
     */
    List<LangueNationaleModel> findByPublieTrueOrderByOrdreAffichageAsc();

    /**
     * Récupère l'ensemble des langues nationales (publiées et brouillons), triées par ordre croissant.
     */
    List<LangueNationaleModel> findAllByOrderByOrdreAffichageAsc();

    /**
     * Recherche une langue par son nom (insensible à la casse).
     */
    Optional<LangueNationaleModel> findByNomIgnoreCase(String nom);

    /**
     * Vérifie l'existence d'une langue par son nom (insensible à la casse).
     */
    boolean existsByNomIgnoreCase(String nom);
}
