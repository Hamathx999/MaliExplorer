package com.maliexplorer_backend.Repository;

import com.maliexplorer_backend.Models.AdministrateurModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<AdministrateurModel, Integer> {
    Optional<AdministrateurModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
