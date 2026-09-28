package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Artisan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtisanRepository extends JpaRepository<Artisan, Integer> {
    Optional<Artisan> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Artisan> findByTypeArtisanatContainingIgnoreCase(String typeArtisanat);
}
