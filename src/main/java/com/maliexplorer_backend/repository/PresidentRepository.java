package com.maliexplorer_backend.Repository;

import com.maliexplorer_backend.Models.President;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresidentRepository extends JpaRepository<President, Long> {

    List<President> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
