package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {

    List<Ville> findByRegionParentIdRegion(Long idRegion);

    List<Ville> findByNomVilleContainingIgnoreCase(String keyword);

    List<Ville> findByEstCapitaleTrue();
}
