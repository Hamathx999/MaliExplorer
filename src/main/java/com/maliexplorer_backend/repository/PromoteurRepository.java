package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.PromoteurModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoteurRepository extends JpaRepository<PromoteurModel, Integer> {
    Optional<PromoteurModel> findByEmail(String email);

    boolean existsByEmail(String email);

    List<PromoteurModel> findByNomOrganisationContainingIgnoreCase(String nomOrganisation);
}
