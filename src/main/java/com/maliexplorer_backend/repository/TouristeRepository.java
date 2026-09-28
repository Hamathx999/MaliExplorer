package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Touriste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TouristeRepository extends JpaRepository<Touriste, Integer> {
    Optional<Touriste> findByEmail(String email);

    boolean existsByEmail(String email);
}
