package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByNomRegionIgnoreCase(String nomRegion);

    boolean existsByNomRegionIgnoreCase(String nomRegion);

    boolean existsByCodeIgnoreCase(String code);

    List<Region> findByNomRegionContainingIgnoreCase(String keyword);
}
