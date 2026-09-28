package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Ethnie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EthnieRepository extends JpaRepository<Ethnie, Long> {

    Optional<Ethnie> findByNomEthnieIgnoreCase(String nomEthnie);

    boolean existsByNomEthnieIgnoreCase(String nomEthnie);

    List<Ethnie> findByNomEthnieContainingIgnoreCase(String keyword);

    List<Ethnie> findByRegionsIdRegion(Long idRegion);
}
