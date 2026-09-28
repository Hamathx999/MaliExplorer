package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.VilleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VilleRepository extends JpaRepository<VilleModel, Long> {

    List<VilleModel> findByRegionParentIdRegion(Long idRegion);

    List<VilleModel> findByNomVilleContainingIgnoreCase(String keyword);

    List<VilleModel> findByEstCapitaleTrue();
}
