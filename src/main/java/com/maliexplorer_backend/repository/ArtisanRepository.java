package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.ArtisanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtisanRepository extends JpaRepository<ArtisanModel, Integer> {
    Optional<ArtisanModel> findByEmail(String email);

    boolean existsByEmail(String email);

    List<ArtisanModel> findByTypeArtisanatContainingIgnoreCase(String typeArtisanat);
}
