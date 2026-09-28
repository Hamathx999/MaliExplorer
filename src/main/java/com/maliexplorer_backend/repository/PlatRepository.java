package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.PlatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatRepository extends JpaRepository<PlatModel, Long> {

    List<PlatModel> findByNomPlatContainingIgnoreCase(String keyword);

    List<PlatModel> findByRegionsIdRegion(Long idRegion);

    List<PlatModel> findByEthniesIdEthnie(Long idEthnie);
}
