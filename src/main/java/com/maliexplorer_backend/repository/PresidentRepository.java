package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.PresidentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresidentRepository extends JpaRepository<PresidentModel, Long> {

    List<PresidentModel> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
