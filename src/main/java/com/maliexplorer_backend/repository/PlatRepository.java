package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {

    List<Plat> findByNomPlatContainingIgnoreCase(String keyword);

    List<Plat> findByRegionsIdRegion(Long idRegion);

    List<Plat> findByEthniesIdEthnie(Long idEthnie);
}
