package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.InfoPratiqueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Référentiel Spring Data JPA pour l'accès aux données des informations pratiques.
 */
@Repository
public interface InfoPratiqueRepository extends JpaRepository<InfoPratiqueModel, Long> {

    /**
     * Récupère toutes les fiches d'informations pratiques publiées, ordonnées selon ordreAffichage.
     */
    List<InfoPratiqueModel> findByPublieTrueOrderByOrdreAffichageAsc();

    /**
     * Récupère l'intégralité des fiches (publiées ou non), ordonnées selon ordreAffichage.
     */
    List<InfoPratiqueModel> findAllByOrderByOrdreAffichageAsc();

    /**
     * Récupère les fiches d'une catégorie donnée (ex: "VISAS", "SANTE"), ordonnées selon ordreAffichage.
     */
    List<InfoPratiqueModel> findByCategorieIgnoreCaseOrderByOrdreAffichageAsc(String categorie);

    /**
     * Recherche une fiche pratique par son titre (insensible à la casse).
     */
    Optional<InfoPratiqueModel> findByTitreIgnoreCase(String titre);

    /**
     * Vérifie si une fiche avec ce titre existe déjà.
     */
    boolean existsByTitreIgnoreCase(String titre);
}
