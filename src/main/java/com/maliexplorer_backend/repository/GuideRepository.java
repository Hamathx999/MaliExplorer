package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.GuideModel;
import com.maliexplorer_backend.model.StatutModeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<GuideModel, Integer> {
    Optional<GuideModel> findByEmail(String email);

    boolean existsByEmail(String email);

    List<GuideModel> findByLangueContainingIgnoreCase(String langue);

    List<GuideModel> findByRecherchePartenariatTrueAndStatutModeration(StatutModeration statut);

    List<GuideModel> findByStatutModeration(StatutModeration statut);
}
