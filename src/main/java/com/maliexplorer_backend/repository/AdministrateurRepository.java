package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Integer> {
    Optional<Administrateur> findByEmail(String email);

    boolean existsByEmail(String email);
}
