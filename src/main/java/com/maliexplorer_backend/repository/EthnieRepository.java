package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.EthnieModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EthnieRepository extends JpaRepository<EthnieModel, Long> {

    Optional<EthnieModel> findByNomEthnieIgnoreCase(String nomEthnie);

    boolean existsByNomEthnieIgnoreCase(String nomEthnie);

    List<EthnieModel> findByNomEthnieContainingIgnoreCase(String keyword);

    List<EthnieModel> findByRegionsIdRegion(Long idRegion);
}
