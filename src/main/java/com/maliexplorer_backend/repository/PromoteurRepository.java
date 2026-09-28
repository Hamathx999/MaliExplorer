package com.maliexplorer_backend.Repository;

import com.maliexplorer_backend.model.Promoteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoteurRepository extends JpaRepository<Promoteur, Integer> {
    Optional<Promoteur> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Promoteur> findByNomOrganisationContainingIgnoreCase(String nomOrganisation);
}
