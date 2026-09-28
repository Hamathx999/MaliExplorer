package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.utilisateurModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface utilisateurRepository extends JpaRepository<utilisateurModel, Integer> {

    Optional<utilisateurModel> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<utilisateurModel> findByFirebaseUid(String firebaseUid);

    boolean existsByFirebaseUid(String firebaseUid);
}
