package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.President;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresidentRepository extends JpaRepository<President, Long> {

    List<President> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
